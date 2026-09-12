import * as fs from 'fs';
import * as path from 'path';

/**
 * 翻译映射接口
 */
interface TranslationMap {
  [key: string]: {
    en?: string;
    'zh-CN'?: string;
  };
}

/**
 * 主函数
 */
async function main() {
  // 定义项目根目录和语言目录
  const rootDir = process.cwd();
  const baseDir = path.join(rootDir, 'src/locales');
  const languages = ['en', 'zh-CN'];
  const translationMap: TranslationMap = {};

  console.log('开始处理翻译文件...');
  console.log(`项目根目录: ${rootDir}`);
  console.log(`语言文件基础目录: ${baseDir}`);

  // 遍历所有语言目录
  for (const lang of languages) {
    console.log(`处理语言: ${lang}`);
    const langDir = path.join(baseDir, lang);
    if (fs.existsSync(langDir)) {
      await traverseDirectory(langDir, lang, translationMap, baseDir);
    } else {
      console.warn(`语言目录不存在: ${langDir}`);
    }
  }

  // 生成CSV内容，添加UTF-8 BOM以解决Excel中文乱码问题
  let csvContent = '\uFEFF' + 'key,en,zh-CN\n';
  
  // 对keys进行排序以保证输出的一致性
  const sortedKeys = Object.keys(translationMap).sort();
  
  for (const key of sortedKeys) {
    const translations = translationMap[key];
    const enContent = translations.en || '';
    const zhContent = translations['zh-CN'] || '';
    
    // 处理CSV中的特殊字符
    const escapedEn = enContent.replace(/"/g, '""');
    const escapedZh = zhContent.replace(/"/g, '""');
    
    csvContent += `"${key}","${escapedEn}","${escapedZh}"\n`;
  }

  // 确保输出目录存在
  const outputDir = path.join(rootDir, 'scripts/output');
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true });
  }

  // 写入CSV文件
  const outputPath = path.join(outputDir, 'translations.csv');
  fs.writeFileSync(outputPath, csvContent, 'utf-8');

  console.log(`处理完成！共提取了 ${sortedKeys.length} 条翻译记录`);
  console.log(`CSV文件已保存至: ${outputPath}`);
}

/**
 * 递归遍历目录
 */
async function traverseDirectory(
  dir: string,
  lang: string,
  translationMap: TranslationMap,
  baseDir: string,
): Promise<void> {
  const files = fs.readdirSync(dir);

  for (const file of files) {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);

    if (stat.isDirectory()) {
      await traverseDirectory(fullPath, lang, translationMap, baseDir);
    } else if (file.endsWith('.json')) {
      processJsonFile(fullPath, lang, translationMap, baseDir);
    }
  }
}

/**
 * 处理单个JSON文件
 */
function processJsonFile(
  filePath: string, 
  lang: string, 
  translationMap: TranslationMap,
  baseDir: string
): void {
  try {
    const content = fs.readFileSync(filePath, 'utf-8');
    const jsonData = JSON.parse(content);
    
    // 计算文件相对于语言目录的路径，用于生成key前缀
    const langDir = path.join(baseDir, lang);
    const relativePath = path.relative(langDir, filePath);
    
    // 移除.json扩展名并将路径分隔符转换为点号
    const pathPrefix = relativePath
      .replace(/\.json$/, '')
      .split(path.sep)
      .join('.');
    
    console.log(`  处理文件: ${relativePath} (前缀: ${pathPrefix})`);
    
    processJsonObject(jsonData, pathPrefix, lang, translationMap);
  } catch (error) {
    console.error(`处理文件 ${filePath} 时出错:`, (error as Error).message);
  }
}

/**
 * 递归处理JSON对象，提取所有键值对
 */
function processJsonObject(
  obj: Record<string, any>,
  prefix: string,
  lang: string,
  translationMap: TranslationMap,
): void {
  for (const key in obj) {
    const fullKey = prefix ? `${prefix}.${key}` : key;
    const value = obj[key];

    if (typeof value === 'object' && value !== null) {
      // 如果值是对象，则递归处理
      processJsonObject(value, fullKey, lang, translationMap);
    } else {
      // 如果值是基本类型，则添加到翻译映射中
      if (!translationMap[fullKey]) {
        translationMap[fullKey] = {};
      }
      translationMap[fullKey][lang as 'en' | 'zh-CN'] = String(value);
    }
  }
}

// 执行主函数
main().catch((err) => {
  console.error('执行过程中发生错误:', err);
  process.exit(1);
});
