function isMobile() {
  const userAgent = navigator.userAgent || navigator.vendor || window.opera;
  const isIOS = /iPad|iPhone|iPod/.test(userAgent) && !window.MSStream;
  const isAndroid = /Mobi|Android/i.test(userAgent);
  const isWindowsPhone = /Windows Phone/i.test(userAgent);
  const isWindowsTablet = /Windows/i.test(userAgent) && !/Mobile/i.test(userAgent);
  const isBlackBerry = /BlackBerry/i.test(userAgent) || /BB10/i.test(userAgent);

  return isIOS || isAndroid || isWindowsPhone || isWindowsTablet || isBlackBerry;
}

function getDeviceType() {
  const userAgent = navigator.userAgent || navigator.vendor || window.opera;
  const isIOS = /iPad|iPhone|iPod/.test(userAgent) && !window.MSStream;
  const isAndroid = /Mobi|Android/i.test(userAgent);
  const isWindowsPhone = /Windows Phone/i.test(userAgent);
  const isWindowsTablet = /Windows/i.test(userAgent) && !/Mobile/i.test(userAgent);
  const isBlackBerry = /BlackBerry/i.test(userAgent) || /BB10/i.test(userAgent);

  return isIOS ? 'iOS' :
         isAndroid ? 'Android' :
         isWindowsPhone ? 'Windows Phone' :
         isWindowsTablet ? 'Windows Tablet' :
         isBlackBerry ? 'BlackBerry' :
         'Desktop';
}

function getBrowserType() {
  const userAgent = navigator.userAgent.toLowerCase();
  if (userAgent.indexOf('chrome') !== -1 && userAgent.indexOf('safari') !== -1) {
    return 'Chrome';
  } else if (userAgent.indexOf('safari') !== -1 && userAgent.indexOf('chrome') === -1) {
    return 'Safari';
  } else if (userAgent.indexOf('firefox') !== -1) {
    return 'Firefox';
  } else if (userAgent.indexOf('edge') !== -1 || userAgent.indexOf('edg') !== -1) {
    return 'Edge';
  } else if (userAgent.indexOf('opera') !== -1 || userAgent.indexOf('opr') !== -1) {
    return 'Opera';
  } else {
    return 'Other';
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const deviceType = getDeviceType();
  const browserType = getBrowserType();
  console.log(`Current Device Type: ${deviceType}`);
  console.log(`Current Browser Type: ${browserType}`);

  const userAgent = navigator.userAgent.toLowerCase();
  const isLineInAppBrowser = userAgent.indexOf('line') !== -1;

  if (isLineInAppBrowser) {
    const url = window.location.href;
    window.location.href = url + (url.indexOf('?') === -1 ? '?' : '&') + 'openExternalBrowser=1';
  }
});
