function setRem() { 
  let designSize = 1920; // 设计图尺寸

    let html = document.documentElement;

    let wW = html.clientWidth;// 窗口宽度
    let rem = wW * 100 / designSize; 

    document.documentElement.style.fontSize = rem + 'px';
}
window.addEventListener('load',setRem)
window.addEventListener('resize',setRem)
