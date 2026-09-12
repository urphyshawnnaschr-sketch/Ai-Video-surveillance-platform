 /**
 * 处理责任人表格数据
 * @param {Object} data - 后端返回的原始数据
 * @returns {Array} 格式化后的表格数据（id不做去重）
 */
export function processResponsiblePersons(data) {
  const {
    firstResponsiblePersonList,
    secondResponsiblePersonList,
    thirdResponsiblePersonList,
  } = data;
  
  // 处理单个层级的责任人数据
  const processLevel = (list, level) => {
    // 存储去重后的责任人信息，使用工号作为键进行去重
    const uniquePersons = {};
    // 存储该层级下所有记录的id（不做去重）
    const ids = [];
    
    // 遍历该层级下的所有记录
    (list || []).forEach(item => {
      // 收集记录id（不做去重，直接添加）
      if (item.id) {
        ids.push(item.id);
      }
      
      // 分割责任人和工号（处理可能的多个值）
      const persons = (item.responsiblePerson || '').split(',');
      const personNos = (item.responsiblePersonNo || '').split(',');
      
      // 遍历每个责任人，添加到去重对象中
      persons.forEach((name, index) => {
        const no = personNos[index] || '';
        // 只处理有工号的记录，避免空值
        if (no && name) {
          uniquePersons[no] = { name, no };
        }
      });
    });
    
    // 转换为数组并提取姓名和工号字符串
    const personList = Object.values(uniquePersons);
    const responsiblePersons = personList.map(p => p.name).join(',');
    const responsiblePersonNos = personList.map(p => p.no).join(',');
    // 将数组转换为逗号分隔的字符串（保留所有id，包括重复的）
    const idsStr = ids.join(',');
    const levelText=level==1?`${ $t('systemManage.alarmPushConfiguration.levelOne')}`:level==2?`${$t('systemManage.alarmPushConfiguration.levelTwo')}`:`${ $t('systemManage.alarmPushConfiguration.levelThree')}`
    
    return {
      level: `${levelText}`,
      responsiblePerson: responsiblePersons || $t('systemManage.alarmPushConfiguration.noContent'),
      responsiblePersonNo: responsiblePersonNos,
      ids: idsStr, // 该层级下所有记录的id（未去重）
      personCount: personList.length,
      groupLevel:level,
    };
  };
  
  // 处理三个层级的数据
  return [
    processLevel(firstResponsiblePersonList, 1),
    processLevel(secondResponsiblePersonList, 2),
    processLevel(thirdResponsiblePersonList, 3)
  ];
}
