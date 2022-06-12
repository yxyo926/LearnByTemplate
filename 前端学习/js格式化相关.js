 //  ------------利用数组格式化----------
var str="border-bottom-color";
function tf(){
  var arr=str.split("-");
  for(var i=1;i<arr.length;i++){
    arr[i]=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
  }
  return arr.join("");
};
tf(str);

// 利用正则表达式格式化
var str="border-bottom-color";
function tf(){
  var re=/-(\w)/g;
  str=str.replace(re,function($0,$1){
    return $1.toUpperCase();
  });
  alert(str)
};
tf(str);