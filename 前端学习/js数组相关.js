//                                        数组去重
// 普通数组去重

// for循环加splice方式
//定义一个有重复数据的数组
let arr = [1,2,1,'j',5,'1',true,2,5,'h',true];
//去重方法
function duplicateRemoval(arr) {
    for(let i = 0;i<arr.length;i++){
        for (let j = i+1; j < arr.length; j++) {
            if (arr[i]===arr[j]) {
            	//删除满足条件的元素
                arr.splice(j,1);
                //因为当前索引值的元素被删除，且后面元素往前移  我们需要将下一次遍历索引从但当前索引开始，即j--
                j--;
            }
        }
    }
}
//方法调用
duplicateRemoval(arr);
//结果验证
console.log(arr);

// 使用set方法 ES6支持
//定义一个有重复数据的数组
let arrset = [1,2,1,'j',5,'1',true,2,5,'h',true];
//定义set对象，并用数组arr对其进行初始化
let set = new Set(arrset);
//通过Array.from()方法将 set 转化为数组 并赋给新数组
let newArr = Array.from(set);
//打印结果
console.log(newArr);

// 使用reduce()和includes()
let arrReduce=[1,2,1,'j',5,'1',true,2,5,'h',true];
function removeDelem(array){
    return array.reduce((o,n)=>o.includes(n)?o:[...o,n],[]);
}
let newArrReduce=removeDelem(arrReduce);
// 打印结果
console.log(newArrReduce);

// 对象数组去重

// for循环与单条件
let arrfor = [
    { id: 0, name: "张三" },
    { id: 1, name: "李四" },
    { id: 2, name: "王五" },
    { id: 3, name: "赵六" },
    { id: 1, name: "孙七" },
    { id: 2, name: "周八" },
    { id: 2, name: "吴九" },
    { id: 3, name: "郑十" },
  ];

  const removeDuplicateObjFor = (arr) => {
    let newArr = []
    let obj = {};
    for (var i = 0; i < arr.length; i++) {
      if (!obj[arr[i].key]) {
        newArr.push(arr[i]);
        obj[arr[i].key] = true;
      }
    }
    return newArr
  };
  console.log(removeDuplicateObjFor(arrfor));

// for循环与多条件
const removeDuplicateObjForMut=(arr)=>{
    let newArrForMut=[];
    let objForMut={};
    // 第二个条件
    let objForMuttwo={};
    for(var i=0;i<arr.length;i++){
        if(!objForMut[arr[i].id]){
            objForMut[arr[i].id]=","+arr[i].name+",";
            newArrForMut.push(arr[i]);
        }else if(objForMut[arr[i].id]&&objForMut[arr[i].id].indexOf(","+arr[i].name+",")==-1){        
                objForMut[arr[i].id]=objForMut[arr[i].id]+","+arr[i].name+",";
                newArrForMut.push(arr[i]);
        }
    }
    return newArrForMut

};
console.log("arrfor"+ JSON.stringify(removeDuplicateObjForMut(arrfor)));

// find 方式去除全部重复 
let arrFind = [
    { id: 0, name: "张三", age: 23 },
    { id: 1, name: "李四", age: 23 },
    { id: 2, name: "王五", age: 24 },
    { id: 3, name: "赵六", age: 25 },
    { id: 1, name: "孙七", age: 23 },
    { id: 2, name: "周八", age: 24 },
    { id: 2, name: "吴九", age: 26 },
    { id: 3, name: "郑十", age: 25 },
  ];

  const removeDuplicateObjFind = (arr) => {
    // 缓存用于记录
    const newArr = [];
    for (const t of arr) {
      // 检查缓存中是否已经存在
      if (
        newArr.find(
          (c) =>
            c.id === t.id &&
            c.age === t.age 
        )
      ) {
        // 已经存在说明以前记录过，现在这个就是多余的，直接忽略
        continue;
      }
      // 不存在就说明以前没遇到过，把它记录下来
      newArr.push(t);
    }

    // 记录结果就是过滤后的结果
    return newArr;
  };

  console.log(JSON.stringify(removeDuplicateObjFind(arrFind)));


// reduce方法去重存一
let arrObject = [{ id: 0, name: "张三" },{ id: 1, name: "李四" },{ id: 2, name: "王五" },{ id: 3, name: "赵六" },
{ id: 1, name: "孙七" },{ id: 2, name: "张三" },{ id: 2, name: "吴九" },{ id: 3, name: "郑十" },];

const removeDuplicateObj = (arr) => {
      let obj = {};
      arr = arr.reduce((newArrObj, next) => {
        obj[next.id] ? "" : (obj[next.id] = true && newArrObj.push(next));
        return newArrObj;
      }, []);
      return arr;
    };
// 打印结果
console.log(removeDuplicateObj(arrObject));

// reduce与多条件
let arrMUt = [
    { id: 0, name: "张三",sex:"test"},
    { id: 1, name: "李四",sex:"test" },
    { id: 2, name: "王五",sex:"test" },
    { id: 3, name: "赵六",sex:"test"},
    { id: 1, name: "孙七",sex:"test" },
    { id: 2, name: "周八",sex:"test" },
    { id: 2, name: "吴九",sex:"test" },
    { id: 3, name: "郑十",sex:"test" },
  ];

    const removeDuplicateObjMut = (arr) => {
      let obj = {};
      arr = arr.reduce((newArrMut, next) => {
        obj[next.id]&&obj[next.name] ? "" : (obj[next.id] = true && newArrMut.push(next));
        return newArrMut;
      }, []);
      return arr;
    };

    console.log(removeDuplicateObj(arr));

    //                                         深拷贝，浅拷贝