var Overload = function(fn_objs){
    var is_match = function(x,y){
        if(x==y)return true;
        if(x.indexOf("*")==-1)return false;
     
        var x_arr = x.split(","),y_arr = y.split(",");
        if(x_arr.length != y_arr.length)return false;
     
        while(x_arr.length){
            var x_first =  x_arr.shift(),y_first = y_arr.shift();
            if(x_first!="*" && x_first!=y_first)return false;
        }
        return true;
    };
    var ret = function(){
        var args = arguments
        ,args_len = args.length
        ,args_types=[]
        ,args_type
        ,fn_objs = args.callee._fn_objs
        ,match_fn = function(){};
         
        for(var i=0;i<args_len;i++){
            var type = typeof args[i];
            type=="object" && (args[i].length>-1) && (type="array");
            args_types.push(type);
        }
        args_type = args_types.join(",");
        for(var k in fn_objs){
            if(is_match(k,args_type)){
                match_fn = fn_objs[k];
                break;
            }
        }
        return match_fn.apply(this,args);
    };
    ret._fn_objs = fn_objs;
    return ret;
};
 
	String.prototype.format = Overload({
    "array" : function(params){
        var reg = /{(\d+)}/gm;
        return this.replace(reg,function(match,name){
            return params[~~name];
        });
    }
    ,"object" : function(param){
        var reg = /{([^{}]+)}/gm;
        return this.replace(reg,function(match,name){
            return param[name];
        });
    }
});

	
	//扩展对象
	var extend=function(o,n,override){
   		for(var p in n)if(n.hasOwnProperty(p) && (!o.hasOwnProperty(p) || override))o[p]=n[p];
	};
	
	
	/**获取unix时间戳*/
	function unixtime(d){
		var time = new Date(d);
		return(time.getTime());
	}
	
	
	/** 
 * 时间对象的格式化; 
 */  
Date.prototype.format = function(format) {  
    /* 
     * eg:format="yyyy-MM-dd hh:mm:ss"; 
     */  
    var o = {  
        "M+" : this.getMonth() + 1, // month  
        "d+" : this.getDate(), // day  
        "h+" : this.getHours(), // hour  
        "m+" : this.getMinutes(), // minute  
        "s+" : this.getSeconds(), // second  
        "q+" : Math.floor((this.getMonth() + 3) / 3), // quarter  
        "S" : this.getMilliseconds()  
        // millisecond  
    }  
  
    if (/(y+)/.test(format)) {  
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4  
                        - RegExp.$1.length));  
    }  
  
    for (var k in o) {  
        if (new RegExp("(" + k + ")").test(format)) {  
            format = format.replace(RegExp.$1, RegExp.$1.length == 1  
                            ? o[k]  
                            : ("00" + o[k]).substr(("" + o[k]).length));  
        }  
    }  
    return format;  
}  

/**检测一个对象是否为空属性*/

function isOwnEmpty(obj)
{
    for(var name in obj)
    {
        if(obj.hasOwnProperty(name))
        {
            return false;
        }
    }
    return true;
};
