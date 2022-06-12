var browser = {
    versions: function () {
        let u = navigator.userAgent;
        return {//移动终端浏览器版本信息
          mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
          ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
          android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
          iPad: u.indexOf('iPad') > -1, //是否iPad
        };
      }(),
    }
    let { versions } = browser
    if (versions.mobile || versions.ios || versions.android || versions.iPad){
    }
    


const dpr = window.devicePixelRatio || window.screen.deviceXDPI / window.screen.logicalXDPI;
const preferWidth = 1750
let zoom=1
if (dpr > 1) { // ⽤户有放⼤，我们再做缩放
  const screenWidth = window.screen.width // 我们是针对屏幕进⾏缩放的，不是针对浏览器
  if (screenWidth < preferWidth) { // 屏幕⼩于预期值才执⾏
    zoom = screenWidth / preferWidth // 得到缩放倍数
    //下⽅细节⾃⾏修改
    document.getElementById('zoom-container').style['transform'] = 'scale(' + zoom + ')'
    document.getElementById('zoom-container').style['transform-origin'] = 'center top'
    document.getElementById('zoom-main').style.height = `calc(${100 / zoom}vh - 101px)`
    document.getElementById('zoom-main').style['min-height'] = `calc(${100 / zoom}vh - 101px)`
  }
}