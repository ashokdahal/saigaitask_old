
var base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
var base64DecodeChars = new Array(
  -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
  -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
  -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
  52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
  -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
  15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
  -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
  41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1);

function base64encode(str) {
  var out, i, len;
  var c1, c2, c3;
  len = str.length;
  i = 0;
  out = "";
  while(i < len) {
  c1 = str.charCodeAt(i++) & 0xff;
  if(i == len)
  {
	  out += base64EncodeChars.charAt(c1 >> 2);
	  out += base64EncodeChars.charAt((c1 & 0x3) << 4);
	  out += "==";
	  break;
  }
  c2 = str.charCodeAt(i++);
  if(i == len)
  {
	  out += base64EncodeChars.charAt(c1 >> 2);
	  out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
	  out += base64EncodeChars.charAt((c2 & 0xF) << 2);
	  out += "=";
	  break;
  }
  c3 = str.charCodeAt(i++);
  out += base64EncodeChars.charAt(c1 >> 2);
  out += base64EncodeChars.charAt(((c1 & 0x3)<< 4) | ((c2 & 0xF0) >> 4));
  out += base64EncodeChars.charAt(((c2 & 0xF) << 2) | ((c3 & 0xC0) >>6));
  out += base64EncodeChars.charAt(c3 & 0x3F);
  }
  return out;
}

function base64decode(str) {
  var c1, c2, c3, c4;
  var i, len, out;
  len = str.length;
  i = 0;
  out = "";
  while(i < len) {
  
  do {
	  c1 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
  } while(i < len && c1 == -1);
  if(c1 == -1)
	  break;
  
  do {
	  c2 = base64DecodeChars[str.charCodeAt(i++) & 0xff];
  } while(i < len && c2 == -1);
  if(c2 == -1)
	  break;
  out += String.fromCharCode((c1 << 2) | ((c2 & 0x30) >> 4));
  
  do {
	  c3 = str.charCodeAt(i++) & 0xff;
	  if(c3 == 61)
	  return out;
	  c3 = base64DecodeChars[c3];
  } while(i < len && c3 == -1);
  if(c3 == -1)
	  break;
  out += String.fromCharCode(((c2 & 0XF) << 4) | ((c3 & 0x3C) >> 2));
  
  do {
	  c4 = str.charCodeAt(i++) & 0xff;
	  if(c4 == 61)
	  return out;
	  c4 = base64DecodeChars[c4];
  } while(i < len && c4 == -1);
  if(c4 == -1)
	  break;
  out += String.fromCharCode(((c3 & 0x03) << 6) | c4);
  }
  return out;
}
function utf16to8(str) {
  var out, i, len, c;
  out = "";
  len = str.length;
  for(i = 0; i < len; i++) {
  c = str.charCodeAt(i);
  if ((c >= 0x0001) && (c <= 0x007F)) {
	  out += str.charAt(i);
  } else if (c > 0x07FF) {
	  out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
	  out += String.fromCharCode(0x80 | ((c >>  6) & 0x3F));
	  out += String.fromCharCode(0x80 | ((c >>  0) & 0x3F));
  } else {
	  out += String.fromCharCode(0xC0 | ((c >>  6) & 0x1F));
	  out += String.fromCharCode(0x80 | ((c >>  0) & 0x3F));
  }
  }
  return out;
}
function utf8to16(str) {
  var out, i, len, c;
  var char2, char3;
  out = "";
  len = str.length;
  i = 0;
  while(i < len) {
  c = str.charCodeAt(i++);
  switch(c >> 4)
  { 
	case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
	  // 0xxxxxxx
	  out += str.charAt(i-1);
	  break;
	case 12: case 13:
	  // 110x xxxx   10xx xxxx
	  char2 = str.charCodeAt(i++);
	  out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
	  break;
	case 14:
	  // 1110 xxxx  10xx xxxx  10xx xxxx
	  char2 = str.charCodeAt(i++);
	  char3 = str.charCodeAt(i++);
	  out += String.fromCharCode(((c & 0x0F) << 12) |
					 ((char2 & 0x3F) << 6) |
					 ((char3 & 0x3F) << 0));
	  break;
  }
  }
  return out;
}
function inputCheckByteLength(str) {
	var byteLen = 0, len = str.length;
	if( !str ) return 0;
	for( var i=0; i<len; i++ )
	byteLen += str.charCodeAt(i) > 255 ? 2 : 1;
	return byteLen;
}

String.prototype.replaceAll = function(s1,s2) { 
    return this.replace(new RegExp(s1,"gm"),s2); 
}

Date.prototype.isLeapYear = function()   
{   
    return (0==this.getYear()%4&&((this.getYear()%100!=0)||(this.getYear()%400==0)));   
}   
  
Date.prototype.Format2 = function(formatStr)   
{   
    var str = formatStr;   
    var Week = [lang.__('Day'),lang.__('1'),lang.__('2'),lang.__('3'),lang.__('4'),lang.__('5'),lang.__('6')];  
  
    str=str.replace(/yyyy|YYYY/,this.getFullYear());   
    str=str.replace(/yy|YY/,(this.getYear() % 100)>9?(this.getYear() % 100).toString():'0' + (this.getYear() % 100));   
  
    str=str.replace(/MM/,this.getMonth()>9?this.getMonth().toString():'0' + this.getMonth());   
    str=str.replace(/M/g,this.getMonth());   
  
    str=str.replace(/w|W/g,Week[this.getDay()]);   
  
    str=str.replace(/dd|DD/,this.getDate()>9?this.getDate().toString():'0' + this.getDate());   
    str=str.replace(/d|D/g,this.getDate());   
  
    str=str.replace(/hh|HH/,this.getHours()>9?this.getHours().toString():'0' + this.getHours());   
    str=str.replace(/h|H/g,this.getHours());   
    str=str.replace(/mm/,this.getMinutes()>9?this.getMinutes().toString():'0' + this.getMinutes());   
    str=str.replace(/m/g,this.getMinutes());   
  
    str=str.replace(/ss|SS/,this.getSeconds()>9?this.getSeconds().toString():'0' + this.getSeconds());   
    str=str.replace(/s|S/g,this.getSeconds());   
  
    return str;   
}   
  
function daysBetween(DateOne,DateTwo)  
{   
    var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ('-'));  
    var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ('-')+1);  
    var OneYear = DateOne.substring(0,DateOne.indexOf ('-'));  
  
    var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ('-'));  
    var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ('-')+1);  
    var TwoYear = DateTwo.substring(0,DateTwo.indexOf ('-'));  
  
    var cha=((Date.parse(OneMonth+'/'+OneDay+'/'+OneYear)- Date.parse(TwoMonth+'/'+TwoDay+'/'+TwoYear))/86400000);   
    return Math.abs(cha);  
}  
  
  
Date.prototype.DateAdd = function(strInterval, Number) {   
    var dtTmp = this;  
    switch (strInterval) {   
        case 's' :return new Date(Date.parse(dtTmp) + (1000 * Number));  
        case 'n' :return new Date(Date.parse(dtTmp) + (60000 * Number));  
        case 'h' :return new Date(Date.parse(dtTmp) + (3600000 * Number));  
        case 'd' :return new Date(Date.parse(dtTmp) + (86400000 * Number));  
        case 'w' :return new Date(Date.parse(dtTmp) + ((86400000 * 7) * Number));  
        case 'q' :return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number*3, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());  
        case 'm' :return new Date(dtTmp.getFullYear(), (dtTmp.getMonth()) + Number, dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());  
        case 'y' :return new Date((dtTmp.getFullYear() + Number), dtTmp.getMonth(), dtTmp.getDate(), dtTmp.getHours(), dtTmp.getMinutes(), dtTmp.getSeconds());  
    }  
}  
  
Date.prototype.DateDiff = function(strInterval, dtEnd) {   
    var dtStart = this;  
    if (typeof dtEnd == 'string' )
    {   
        dtEnd = 我(dtEnd);  
    }  
    switch (strInterval) {   
        case 's' :return parseInt((dtEnd - dtStart) / 1000);  
        case 'n' :return parseInt((dtEnd - dtStart) / 60000);  
        case 'h' :return parseInt((dtEnd - dtStart) / 3600000);  
        case 'd' :return parseInt((dtEnd - dtStart) / 86400000);  
        case 'w' :return parseInt((dtEnd - dtStart) / (86400000 * 7));  
        case 'm' :return (dtEnd.getMonth()+1)+((dtEnd.getFullYear()-dtStart.getFullYear())*12) - (dtStart.getMonth()+1);  
        case 'y' :return dtEnd.getFullYear() - dtStart.getFullYear();  
    }  
}  
  
Date.prototype.toStringT = function(showWeek)  
{   
    var myDate= this;  
    var str = myDate.toLocaleDateString();  
    if (showWeek)  
    {   
        var Week = [lang.__('Day'),lang.__('1'),lang.__('2'),lang.__('3'),lang.__('4'),lang.__('5'),lang.__('6')];  
        str += ' Week' + Week[myDate.getDay()];  
    }  
    return str;  
}  
  
function IsValidDate(DateStr)   
{   
    var sDate=DateStr.replace(/(^\s+|\s+$)/g,'');
    if(sDate=='') return true;   
    var s = sDate.replace(/[\d]{ 4,4 }[\-/]{ 1 }[\d]{ 1,2 }[\-/]{ 1 }[\d]{ 1,2 }/g,'');   
    if (s=='')
    {   
        var t=new Date(sDate.replace(/\-/g,'/'));   
        var ar = sDate.split(/[-/:]/);   
        if(ar[0] != t.getYear() || ar[1] != t.getMonth()+1 || ar[2] != t.getDate())   
        {   
            //alert('err');   
            return false;   
        }   
    }   
    else   
    {   
        //alert('err');   
        return false;   
    }   
    return true;   
}   
  
//+---------------------------------------------------  
function CheckDateTime(str)  
{   
    var reg = /^(\d+)-(\d{ 1,2 })-(\d{ 1,2 }) (\d{ 1,2 }):(\d{ 1,2 }):(\d{ 1,2 })$/;   
    var r = str.match(reg);   
    if(r==null)return false;   
    r[2]=r[2]-1;   
    var d= new Date(r[1],r[2],r[3],r[4],r[5],r[6]);   
    if(d.getFullYear()!=r[1])return false;   
    if(d.getMonth()!=r[2])return false;   
    if(d.getDate()!=r[3])return false;   
    if(d.getHours()!=r[4])return false;   
    if(d.getMinutes()!=r[5])return false;   
    if(d.getSeconds()!=r[6])return false;   
    return true;   
}   
  
Date.prototype.toArray = function()  
{   
    var myDate = this;  
    var myArray = Array();  
    myArray[0] = myDate.getFullYear();  
    myArray[1] = myDate.getMonth();  
    myArray[2] = myDate.getDate();  
    myArray[3] = myDate.getHours();  
    myArray[4] = myDate.getMinutes();  
    myArray[5] = myDate.getSeconds();  
    return myArray;  
}  
  
Date.prototype.DatePart = function(interval)  
{   
    var myDate = this;  
    var partStr='';  
    var Week = [lang.__('Day'),lang.__('1'),lang.__('2'),lang.__('3'),lang.__('4'),lang.__('5'),lang.__('6')];  
    switch (interval)  
    {   
        case 'y' :partStr = myDate.getFullYear();break;  
        case 'm' :partStr = myDate.getMonth()+1;break;  
        case 'd' :partStr = myDate.getDate();break;  
        case 'w' :partStr = Week[myDate.getDay()];break;  
        case 'ww' :partStr = myDate.WeekNumOfYear();break;  
        case 'h' :partStr = myDate.getHours();break;  
        case 'n' :partStr = myDate.getMinutes();break;  
        case 's' :partStr = myDate.getSeconds();break;  
    }  
    return partStr;  
}  
  
Date.prototype.MaxDayOfDate = function()  
{   
    var myDate = this;  
    var ary = myDate.toArray();  
    var date1 = (new Date(ary[0],ary[1]+1,1));  
    var date2 = date1.dateAdd(1,'m',1);  
    var result = dateDiff(date1.Format('yyyy-MM-dd'),date2.Format('yyyy-MM-dd'));  
    return result;  
}  
  
Date.prototype.WeekNumOfYear = function()  
{   
    var myDate = this;  
    var ary = myDate.toArray();  
    var year = ary[0];  
    var month = ary[1]+1;  
    var day = ary[2];  
    document.write('< script language=VBScript\> \n');  
    document.write("myDate = DateValue(''+month+'-'+day+'-'+year+'') \n");  
    document.write("result = DatePart('ww', myDate) \n");  
    document.write(' \n');  
    return result;  
}  
  
function StringToDate(DateStr)  
{   
	//alert("Start");
    var converted = Date.parse(DateStr);  
	//alert(converted);
    var myDate = new Date(converted);  
	//alert(myDate);
    if (isNaN(myDate))  
    {   
        //var delimCahar = DateStr.indexOf('/')!=-1?'/':'-';  
        var arys= DateStr.split('-');  
		//alert(arys);
        myDate = new Date(arys[0],--arys[1],arys[2]);  
        //alert(myDate);
    }  
    return myDate;  
} 

Date.prototype.format = function(format) {     
    /*   
     * eg:format="YYYY-MM-dd hh:mm:ss";   
     */    
    var o = {     
        "M+" :this.getMonth() + 1, // month     
        "d+" :this.getDate(), // day     
        "h+" :this.getHours(), // hour     
        "m+" :this.getMinutes(), // minute     
        "s+" :this.getSeconds(), // second     
        "q+" :Math.floor((this.getMonth() + 3) / 3), // quarter     
        "S" :this.getMilliseconds()     
    // millisecond     
    }     
    
    if (/(y+)/.test(format)) {     
        format = format.replace(RegExp.$1, (this.getFullYear() + "")     
                .substr(4 - RegExp.$1.length));     
    }     
    
    for ( var k in o) {     
        if (new RegExp("(" + k + ")").test(format)) {     
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]     
                    : ("00" + o[k]).substr(("" + o[k]).length));     
        }     
    }     
    return format;     
}    

function formatNumber(num,pattern){   
  var strarr = num?num.toString().split('.'):['0'];   
  var fmtarr = pattern?pattern.split('.'):[''];   
  var retstr='';   
  

  var str = strarr[0];   
  var fmt = fmtarr[0];   
  var i = str.length-1;     
  var comma = false;   
  for(var f=fmt.length-1;f>=0;f--){   
    switch(fmt.substr(f,1)){   
      case '#':   
        if(i>=0 ) retstr = str.substr(i--,1) + retstr;   
        break;   
      case '0':   
        if(i>=0) retstr = str.substr(i--,1) + retstr;   
        else retstr = '0' + retstr;   
        break;   
      case ',':   
         comma = true;   
         retstr=','+retstr;   
        break;   
     }   
   }   
  if(i>=0){   
    if(comma){   
      var l = str.length;   
      for(;i>=0;i--){   
         retstr = str.substr(i,1) + retstr;   
        if(i>0 && ((l-i)%3)==0) retstr = ',' + retstr;   
       }   
     }   
    else retstr = str.substr(0,i+1) + retstr;   
   }   
  
   retstr = retstr+'.';   
 
   str=strarr.length>1?strarr[1]:'';   
   fmt=fmtarr.length>1?fmtarr[1]:'';   
   i=0;   
  for(var f=0;f<fmt.length;f++){   
    switch(fmt.substr(f,1)){   
      case '#':   
        if(i<str.length) retstr+=str.substr(i++,1);   
        break;   
      case '0':   
        if(i<str.length) retstr+= str.substr(i++,1);   
        else retstr+='0';   
        break;   
     }   
   }   
  return retstr.replace(/^,+/,'').replace(/\.$/,'');   
}   

function DateTime(year, month, day, hour, min, sec, millisec){ 
    var d = new Date(); 

    if (year || year == 0){ 
        d.setFullYear(year); 
    } 
    if (month || month == 0){ 
        d.setMonth(month - 1); 
    } 
    if (day || day == 0){ 
        d.setDate(day); 
    } 
    if (hour || hour == 0){ 
        d.setHours(hour); 
    } 
    if (min || min == 0){ 
        d.setMinutes(min); 
    } 
    if (sec || sec == 0){ 
        d.setSeconds(sec); 
    } 
    if (millisec || millisec == 0){ 
        d.setMilliseconds(millisec); 
    } 
    this.AddDays = function(value){ 
        if(!ValidateAddMethodParam(value)){ 
            return null; 
        } 
        var result = this.Clone(); 
        result.GetValue().setDate(result.GetDay() + value); 
        return result; 
    } 
    this.AddHours = function(value){ 
        if(!ValidateAddMethodParam(value)){ 
            return null; 
        } 
        var result = this.Clone(); 
        result.GetValue().setHours(result.GetHour() + value); 
        return result; 
    } 
    this.AddMinutes = function(value){ 
        if(!ValidateAddMethodParam(value)){ 
            return null; 
        } 
        var result = this.Clone(); 
        result.GetValue().setMinutes(result.GetMinute() + value); 
        return result; 
    } 
    this.AddMilliseconds = function(value){ 
        if(!ValidateAddMethodParam(value)){ 
            return null; 
        } 
        var result = this.Clone(); 
        result.GetValue().setMilliseconds(result.GetMillisecond() + value); 
        return result; 
    } 
    this.AddMonths = function(value){ 
        if(!ValidateAddMethodParam(value)){ 
            return null; 
        } 
        var result = this.Clone(); 
        result.GetValue().setMonth(result.GetValue().getMonth() + value); 
        return result; 
    } 
    this.AddSeconds = function(value){ 
        if(!ValidateAddMethodParam(value)){ 
            return null; 
        } 
        var result = this.Clone(); 
        result.GetValue().setSeconds(result.GetSecond() + value); 
        return result; 
    } 
    this.AddYears = function(value){ 
        if(!ValidateAddMethodParam(value)){ 
            return null; 
        } 
        var result = this.Clone(); 
        result.GetValue().setFullYear(result.GetYear() + value); 
        return result; 
    }     
    this.CompareTo = function(other){ 
        var internalTicks = other.getTime(); 
        var num2 = d.getTime(); 
     if (num2 > internalTicks) 
     { 
     return 1; 
     } 
     if (num2 < internalTicks) 
     { 
     return -1; 
     } 
     return 0; 
    } 
    this.Clone = function(){ 
        return new DateTime( 
             this.GetYear() 
            ,this.GetMonth() 
            ,this.GetDay() 
            ,this.GetHour() 
            ,this.GetMinute() 
            ,this.GetSecond() 
            ,this.GetMillisecond()); 
    } 
    this.Equals = function(other){ 
        return this.CompareTo(other) == 0; 
    } 
    this.GetDate = function(){ 
        var result = new DateTime(d.getFullYear(), d.getMonth(), d.getDate(), 0, 0, 0, 0); 
        return result ; 
    } 
    this.GetDay = function(){ 
        return d.getDate(); 
    } 
    this.GetDayOfWeek = function(){ 
        return d.getDay(); 
    } 
    this.GetHour = function(){ 
        return d.getHours(); 
    } 
    this.GetMinute = function(){ 
        return d.getMinutes(); 
    } 
    this.GetMillisecond = function(){ 
        return d.getMilliseconds(); 
    } 
    this.GetMonth = function(){ 
        return d.getMonth() + 1; 
    } 
    this.GetNextMonthFirstDay = function(){ 
        var result = new DateTime(this.GetYear(), this.GetMonth(), 1, 0, 0, 0, 0); 
        result = result.AddMonths(1); 
        return result; 
    } 
    this.GetNextWeekFirstDay = function(){ 
        var result = this.GetDate(); 
        return result.AddDays(7 - result.GetDayOfWeek()); 
    } 
    this.GetNextYearFirstDay = function(){ 
        return new DateTime(this.GetYear() + 1, 1, 1, 0, 0, 0, 0); 
    } 
    this.GetSecond = function(){ 
        return d.getSeconds(); 
    } 
    this.GetValue = function(){ 
        return d; 
    } 
    this.GetYear = function(){ 
        return d.getFullYear(); 
    } 
    this.IsDateTime = function(){} 
    this.ToShortDateString = function(){ 
        var result = ""; 
        result = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate(); 
        return result;     
    } 
    this.ToShortTimeString = function(){ 
        var result = ""; 
        result = d.getHours() + ":" + d.getMinutes() + ":" + d.getSeconds(); 
        return result;     
    } 
    this.ToString = function(format){ 
        if(typeof(format) == "string"){ 

        } 
        return this.ToShortDateString() + " " + this.ToShortTimeString(); 
    } 
    function ValidateAddMethodParam(param){ 
        if(typeof(param) != "number"){ 
            return false; 
        } 
        return true; 
    } 
    this.getTime = function(){ 
        return d.getTime(); 
    } 
} 


//使用var myuuid=new UUID();
//On creation of a UUID object, set it's initial value
function UUID(){
 this.id = this.createUUID();
}



//When asked what this Object is, lie and return it's value
UUID.prototype.valueOf = function(){ return this.id; };
UUID.prototype.toString = function(){ return this.id; };



//
//INSTANCE SPECIFIC METHODS
//
UUID.prototype.createUUID = function(){
 //
 // Loose interpretation of the specification DCE 1.1: Remote Procedure Call
 // since JavaScript doesn't allow access to internal systems, the last 48 bits
 // of the node section is made up using a series of random numbers (6 octets long).
 // 
 var dg = new Date(1582, 10, 15, 0, 0, 0, 0);
 var dc = new Date();
 var t = dc.getTime() - dg.getTime();
 var tl = UUID.getIntegerBits(t,0,31);
 var tm = UUID.getIntegerBits(t,32,47);
 var thv = UUID.getIntegerBits(t,48,59) + '1'; // version 1, security version is 2
 var csar = UUID.getIntegerBits(UUID.rand(4095),0,7);
 var csl = UUID.getIntegerBits(UUID.rand(4095),0,7);

 // since detection of anything about the machine/browser is far to buggy,
 // include some more random numbers here
 // if NIC or an IP can be obtained reliably, that should be put in
 // here instead.
 var n = UUID.getIntegerBits(UUID.rand(8191),0,7) +
         UUID.getIntegerBits(UUID.rand(8191),8,15) +
         UUID.getIntegerBits(UUID.rand(8191),0,7) +
         UUID.getIntegerBits(UUID.rand(8191),8,15) +
         UUID.getIntegerBits(UUID.rand(8191),0,15); // this last number is two octets long
 return tl + tm  + thv  + csar + csl + n;
};



//Pull out only certain bits from a very large integer, used to get the time
//code information for the first part of a UUID. Will return zero's if there
//aren't enough bits to shift where it needs to.
UUID.getIntegerBits = function(val,start,end){
var base16 = UUID.returnBase(val,16);
var quadArray = new Array();
var quadString = '';
var i = 0;
for(i=0;i<base16.length;i++){
  quadArray.push(base16.substring(i,i+1));   
}
for(i=Math.floor(start/4);i<=Math.floor(end/4);i++){
  if(!quadArray[i] || quadArray[i] == '') quadString += '0';
  else quadString += quadArray[i];
}
return quadString;
};



//Replaced from the original function to leverage the built in methods in
//JavaScript. Thanks to Robert Kieffer for pointing this one out
UUID.returnBase = function(number, base){
return (number).toString(base).toUpperCase();
};



//pick a random number within a range of numbers
//int b rand(int a); where 0 <= b <= a
UUID.rand = function(max){
return Math.floor(Math.random() * (max + 1));
};

function set_label_value_by_id_com(id, val)
{
	var label=document.getElementById(id); 
	label.innerText=val; 
	$("#" + id).html(val); 	
}

function decode(s) {
    return unescape(s.replace(/\\(u[0-9a-fA-F]{4})/gm, '%$1'));
}

function replace_u_unicode_string(id) {
	//document.getElementById("alarm_init_combox").innerHTML= unescape(document.getElementById("alarm_init_combox").innerHTML.replace(/\\/g,'%'));
	//console.log("replace_u_unicode_string = " + document.getElementById(id).innerHTML);
	//document.getElementById(id).innerHTML= unescape(document.getElementById(id).innerHTML.replace(/\\/g,'%'));
	document.getElementById(id).innerHTML= decode(document.getElementById(id).innerHTML);
}
function replace_u_unicode_string_process_report(id) {
	//document.getElementById("alarm_init_combox").innerHTML= unescape(document.getElementById("alarm_init_combox").innerHTML.replace(/\\/g,'%'));
	//console.log("replace_u_unicode_string = " + document.getElementById(id).innerHTML);
	//v = v.replace(/\\n/g,'<br/>')
	var v = document.getElementById(id).innerHTML;
	v = v.replace(/\\n/g,'\r\n')
	document.getElementById(id).innerHTML= decode(v);
}



/*
半角
A－Z　　　　u0000 - u00FF
半角日本語　uFF61 - uFF9F
半角そのた　uFFE8 – uFFEE

全角
全角数字(0-9) 　uFF10 - uFF19
全角(A-Z): 　　	uFF21 - uFF3A
全角(a-z): 		uFF41 - uFF5A
全角平仮名　　	u3040 - u309F
全角片仮名　	u30A0 - u30FF
全角Latin: 		uFF01 - uFF5E
全角Symbol: 	uFFE0 - uFFE5

判断方法

半角
·u0020 – 007E  ASSCII
·uFF61 –uFF9F 
*/

String.prototype.isBytesSaigai= function() {
	var cArr =this.match(/[^\x00-\xff|\uff61-\uff9f]/ig);
	return (cArr==null ? true : false);
}

function getBytesSaigai(v)
{
	var len = v.length;
	var cntH = 0;
	var cntZ = 0;
	for(var i = 0; i < len; i++) {
		if(v.charAt(i).isBytesSaigai() == true) {
			cntH++;
		} else {
			cntZ++;	
		}
	}
	return cntH + cntZ*2;
}

function getCharNumSaigai(v)
{
	var len = v.length;
	var cntH = 0;
	var cntZ = 0;
	for(var i = 0; i < len; i++) {
		if(v.charAt(i).isBytesSaigai() == true) {
			cntH++;
		} else {
			cntZ++;	
		}
	}
	return cntH + cntZ;
}
