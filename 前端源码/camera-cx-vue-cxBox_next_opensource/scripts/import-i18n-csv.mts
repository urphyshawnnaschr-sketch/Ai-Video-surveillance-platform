import * as fs from 'fs';
import * as path from 'path';

/**
 * CSV行数据接口
 */
interface CsvRow {
  key: string;
  en: string;
  zhCN: string;
}

/**
 * 翻译数据接口
 */
interface TranslationData {
  [language: string]: {
    [filePath: string]: Record<string, any>;
  };
}

/**
 * 主函数
 */
async function main() {
  // 定义项目根目录和文件路径
  const rootDir = process.cwd();
  const csvPath = path.join(rootDir, 'scripts/i18n.csv');
  const baseDir = path.join(rootDir, 'src/locales');
  const languages = ['en', 'zh-CN'];

  console.log('开始导入翻译文件...');
  console.log(`项目根目录: ${rootDir}`);
  console.log(`CSV文件路径: ${csvPath}`);
  console.log(`语言文件基础目录: ${baseDir}`);

  // 检查CSV文件是否存在
  if (!fs.existsSync(csvPath)) {
    console.error(`CSV文件不存在: ${csvPath}`);
    process.exit(1);
  }

  // 读取并解析CSV文件
  const csvData = await parseCsvFile(csvPath);
  console.log(`从CSV文件中读取了 ${csvData.length} 条翻译记录`);

  // 组织翻译数据
  const translationData: TranslationData = organizeTranslationData(csvData);

  // 更新JSON文件
  for (const lang of languages) {
    console.log(`更新语言: ${lang}`);
    const langDir = path.join(baseDir, lang);
    
    if (!fs.existsSync(langDir)) {
      console.log(`创建语言目录: ${langDir}`);
      fs.mkdirSync(langDir, { recursive: true });
    }

    const langData = translationData[lang];
    if (langData) {
      await updateJsonFiles(langDir, langData);
    }
  }

  console.log('翻译文件导入完成！');
}

/**
 * 解析CSV文件
 */
async function parseCsvFile(csvPath: string): Promise<CsvRow[]> {
  const content = fs.readFileSync(csvPath, 'utf-8');
  
  // 移除BOM
  let csvContent = content;
  if (csvContent.startsWith('\uFEFF')) {
    csvContent = csvContent.substring(1);
  }

  const csvData: CsvRow[] = [];
  const rows = parseCsvContent(csvContent);

  // 跳过标题行（如果存在）
  let startIndex = 0;
  if (rows.length > 0 && rows[0].length >= 3) {
    const firstRow = rows[0];
    if (firstRow[0].toLowerCase().includes('key') && 
        firstRow[1].toLowerCase().includes('en')) {
      startIndex = 1;
    }
  }

  for (let i = startIndex; i < rows.length; i++) {
    const row = rows[i];
    if (row.length >= 3 && row[0].trim()) {
      csvData.push({
        key: row[0].trim(),
        en: row[1].trim(),
        zhCN: row[2].trim()
      });
    }
  }

  return csvData;
}

/**
 * 解析整个CSV内容，正确处理跨行的引号字段
 */
function parseCsvContent(content: string): string[][] {
  const rows: string[][] = [];
  let currentRow: string[] = [];
  let currentField = '';
  let inQuotes = false;
  let i = 0;

  while (i < content.length) {
    const char = content[i];
    
    if (char === '"') {
      if (inQuotes && content[i + 1] === '"') {
        // 转义的引号
        currentField += '"';
        i += 2;
      } else {
        // 开始或结束引号
        inQuotes = !inQuotes;
        i++;
      }
    } else if (char === ',' && !inQuotes) {
      // 字段分隔符
      currentRow.push(currentField);
      currentField = '';
      i++;
    } else if ((char === '\n' || char === '\r') && !inQuotes) {
      // 行分隔符（只有在非引号内才是真正的行分隔符）
      if (currentField || currentRow.length > 0) {
        currentRow.push(currentField);
        if (currentRow.length > 0) {
          rows.push(currentRow);
        }
        currentRow = [];
        currentField = '';
      }
      
      // 处理 \r\n 的情况
      if (char === '\r' && content[i + 1] === '\n') {
        i += 2;
      } else {
        i++;
      }
    } else {
      // 普通字符（包括引号内的换行符）
      currentField += char;
      i++;
    }
  }
  
  // 处理最后一个字段和行
  if (currentField || currentRow.length > 0) {
    currentRow.push(currentField);
    if (currentRow.length > 0) {
      rows.push(currentRow);
    }
  }

  return rows;
}

/**
 * 组织翻译数据
 */
function organizeTranslationData(csvData: CsvRow[]): TranslationData {
  const translationData: TranslationData = {
    'en': {},
    'zh-CN': {}
  };

  for (const row of csvData) {
    const keyParts = row.key.split('.');
    
    // 确定文件路径和对象路径
    const { filePath, objectPath } = determineFileAndObjectPath(keyParts);
    
    // 为每种语言组织数据
    if (row.en) {
      if (!translationData['en'][filePath]) {
        translationData['en'][filePath] = {};
      }
      setNestedValue(translationData['en'][filePath], objectPath, row.en);
    }

    if (row.zhCN) {
      if (!translationData['zh-CN'][filePath]) {
        translationData['zh-CN'][filePath] = {};
      }
      setNestedValue(translationData['zh-CN'][filePath], objectPath, row.zhCN);
    }
  }

  return translationData;
}

/**
 * 确定文件路径和对象路径
 */
function determineFileAndObjectPath(keyParts: string[]): { filePath: string; objectPath: string[] } {
  // 假设最后一个部分是对象的键，前面的部分构成文件路径
  // 但需要智能判断，因为可能存在多层嵌套的对象
  
  let filePath = '';
  let objectPath: string[] = [];

  if (keyParts.length === 1) {
    // 如果只有一个部分，假设是根目录下的文件
    filePath = keyParts[0] + '.json';
    objectPath = [];
  } else {
    // 尝试找到合理的分割点
    // 通常文件名部分不会太深，对象路径可能更深
    
    // 先假设第一个部分是文件名
    filePath = keyParts[0] + '.json';
    objectPath = keyParts.slice(1);
    
    // 如果第一个部分看起来像目录名（如components、form等），则包含更多路径
    const firstPart = keyParts[0];
    if (firstPart === 'components' || firstPart === 'form' || keyParts.length > 2) {
      // 取前面部分作为文件路径
      const pathDepth = Math.min(keyParts.length - 1, 3); // 最多3层目录深度
      filePath = keyParts.slice(0, pathDepth).join('/') + '.json';
      objectPath = keyParts.slice(pathDepth);
    }
  }

  return { filePath, objectPath };
}

/**
 * 设置嵌套对象的值
 */
function setNestedValue(obj: Record<string, any>, path: string[], value: string): void {
  if (path.length === 0) return;
  
  let current = obj;
  for (let i = 0; i < path.length - 1; i++) {
    const key = path[i];
    if (!(key in current) || typeof current[key] !== 'object') {
      current[key] = {};
    }
    current = current[key];
  }
  
  current[path[path.length - 1]] = value;
}

/**
 * 更新JSON文件
 */
async function updateJsonFiles(langDir: string, langData: Record<string, any>): Promise<void> {
  for (const [filePath, data] of Object.entries(langData)) {
    const fullPath = path.join(langDir, filePath);
    const dirPath = path.dirname(fullPath);
    
    // 确保目录存在
    if (!fs.existsSync(dirPath)) {
      fs.mkdirSync(dirPath, { recursive: true });
    }
    
    // 如果文件已存在，先读取现有内容并合并
    let existingData = {};
    if (fs.existsSync(fullPath)) {
      try {
        const existingContent = fs.readFileSync(fullPath, 'utf-8');
        existingData = JSON.parse(existingContent);
      } catch (error) {
        console.warn(`读取现有文件失败 ${fullPath}:`, (error as Error).message);
      }
    }
    
    // 深度合并数据
    const mergedData = deepMerge(existingData, data);
    
    // 写入JSON文件
    const jsonContent = JSON.stringify(mergedData, null, 2);
    fs.writeFileSync(fullPath, jsonContent, 'utf-8');
    
    console.log(`  已更新: ${filePath}`);
  }
}

/**
 * 深度合并对象
 */
function deepMerge(target: any, source: any): any {
  if (typeof target !== 'object' || target === null) {
    return source;
  }
  
  if (typeof source !== 'object' || source === null) {
    return target;
  }
  
  const result = { ...target };
  
  for (const key in source) {
    if (typeof source[key] === 'object' && source[key] !== null && 
        typeof result[key] === 'object' && result[key] !== null) {
      result[key] = deepMerge(result[key], source[key]);
    } else {
      result[key] = source[key];
    }
  }
  
  return result;
}

// 执行主函数
main().catch((err) => {
  console.error('执行过程中发生错误:', err);
  process.exit(1);
});
