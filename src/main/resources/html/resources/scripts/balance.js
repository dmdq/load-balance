/**
 * 显示黑白名单配置界面
 */
function show_forbidden(btn){
	//过滤类型 : 不过滤,白名单,黑名单
	var forbidden_type = $(btn).prev().prev();
	//过滤的配置
	var forbidden_value = $(btn).prev();
	//过滤类型
	var ft = forbidden_type.val();
	//过滤值
	var fv = forbidden_value.val();
	
	if(fv!=""){

		$("#action_config ul li[id!='frist_config']").remove();
		
		$("#frist_config input").val("");
		
		var fv_items = fv.split(",");
		//是否能使用第一个节点元素
		var canUseFrist = true;
		
		//循环所有存在的ip配置列表
		for(var fv_index in fv_items){
			
			//获取当前配置的ip
			var fv_item = fv_items[fv_index];
			
			//详情配置
			var config_detials = fv_item.split(" - ");
			
			if(fv_index>0){
				add_forbidden_config($("#add_forbidden_config"));
			}
			//为2段配置的
			if(config_detials.length==2){
				//第一段
				var before = config_detials[0];
				//第二段
				var after = config_detials[1];
				
				if($("#action_config ul").find('li:eq('+fv_index+')').find("span").length>=2){
					
				}else{
					var addBetween = $("#action_config ul").find('li:eq('+fv_index+')').find("a:eq(0)");
					
					console.log(addBetween);
					
					add_forbidden_between(addBetween);
				}
				//$("#forbidden_config .ip_field").val("*");
				
				var before_fields = before.split(".");
				var after_fields = after.split(".");
				
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(0)").val(before_fields[0]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(1)").val(before_fields[1]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(2)").val(before_fields[2]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(3)").val(before_fields[3]);
				
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(4)").val(after_fields[0]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(5)").val(after_fields[1]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(6)").val(after_fields[2]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(7)").val(after_fields[3]);
				
			}else{
				var before_fields = fv_item.split(".");
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(0)").val(before_fields[0]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(1)").val(before_fields[1]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(2)").val(before_fields[2]);
				$("#action_config ul").find('li:eq('+fv_index+')').find(".ip_field:eq(3)").val(before_fields[3]);
			}
			
		}
	}else{//没有配置ip过滤
		$("#action_config ul li[id!='frist_config']").remove();
		$("#frist_config input").val("");
	}
	//选中之前pei'zh
	$("input:radio[name='forbidden_type']")[ft].checked = true;
	
	//页面层例二
	i = $.layer({
		type : 1,
		title : false,
		closeBtn : false,
		border : [ 5, 0.5, '#666', true ],
		offset : [ '0px', '' ],
		move : [ '.forbidden_config', true ],
		area : [ '620px', 'auto' ],
		page : {
			dom : '#forbidden_config',
		},
		success : function() {
			layer.shift('top', 500);
		}
	});
	var index = i;
	$('.closeBtn').on('click', function() {
		if (i == index) {
			layer.close(i);
			console.log("closeBtn : " + i);
		}
	});
	$(".submit_forbidden").click(function() {
		if (i == index) {
			console.log("submit_forbidden : "+ i);
			if (submit_forbidden(btn)) {
				layer.close(i);
			}
		}
	});
}
/**
 * 编辑过滤器
 */
function edit_forbidden(obj){
	show_forbidden(obj);
}
/**
 * 修改显示名称
 */
 function change_display(obj){
	 	var current = $(obj);
		current.attr("contentEditable","true"); 
		var owner = current.parent().parent().parent();
		var ownerId = owner.attr("id");
		var current_name = current.html();
		var current_id = current.attr("id");
		var canChange = true;
		$("#"+ownerId+" .display").each(function(){
			var uuid = $(this).attr("id");
			if(uuid!=current_id){
				var name = $(this).html();
				//是否有相同名称
				if(name==current_name){
					msgbox("已经存在该名称:"+name);
					var src_title = current.attr("title");
					current.html(src_title);
					canChange = false;
					return false;
				}
			}
		});
		if(canChange){
			current.attr("title",current_name);
		}
}
/**
 * 提交更新黑白名单配置
 */
function submit_forbidden(from){
	var forbidden_type_val  = parseInt($.trim($('input[name="forbidden_type"]:checked').val()));
	if(forbidden_type_val==0){
		console.log("close forbidden");
		$(from).prev().val("");
		$(from).prev().prev().val("0");
		$(from).html("不过滤"); 
		return true;
	}
	var flag = true;
	var forbidden_items = new Array();
	$("#action_config li").each(function(){
		var ip_li = $(this);
		var ip_fields_list = ip_li.children("span");
		var forbidden_item_between = new Array();
		$(ip_li.children("span")).each(function(){
			var ip_fields = $(this);
			var forbidden_item = new Array();
			$(ip_fields.children("input")).each(function(index){
				var ip = $.trim($(this).val());
				if(ip==""){
					msgbox("ip字段不能为空");
					flag &= false;
					return false;
				}
				if(ip!="*"){
					if(isNaN(ip)){
						msgbox("ip字段只能为 <span class='red'>数字</span> 或 <span class='red'>*</span> 号");
						flag &= false;
						return ;
					}else{
						//当为ip数值第一个时候
						if(index==0||index==3){
							if(parseInt(ip)>255||parseInt(ip)<1){
								msgbox("ip起始,结束数字为: 1 - 255 !");
								flag &= false;
								return ;
							}
						}else{
							if(parseInt(ip)>255||parseInt(ip)<0){
								msgbox("ip为数字时: 0 - 255 !");
								flag &= false;
								return ;
							}
						}
					}
				}
				forbidden_item.push(ip);
			});
			//添加到区间范围内
			forbidden_item_between.push(forbidden_item);
		});
		//区间范围内的ip
		if(forbidden_item_between.length==2){
			var start = forbidden_item_between[0];
			var end = forbidden_item_between[1];
			if(start.length==4&&end.length==4){
				var ip_info = start[0]+"."+start[1]+"."+start[2]+"."+start[3]+" - "+end[0]+"."+end[1]+"."+end[2]+"."+end[3];
				forbidden_items.push(ip_info);
			}
		}else{//默认只有一个段位
			var start = forbidden_item_between[0];
			if(start.length==4){
				var ip_info = start[0]+"."+start[1]+"."+start[2]+"."+start[3];
				forbidden_items.push(ip_info);
			}
		}
	});
	if(flag){
		//TODO
		var forbidden_type = $('#forbidden_config input[name="forbidden_type"]:checked').val();
		console.log(forbidden_items);
		$("#forbidden_value").val(forbidden_items);
		$(from).prev().val(forbidden_items);
		$(from).prev().prev().val(forbidden_type_val);
		$(from).html(forbidden_type_val==1?"白名单":"黑名单");
	}
	return flag;
}

/**
 * 删除过滤配置行
 */
function drop_forbidden_config(obj){
	 $(obj).parent().remove();
}
/**
 * 添加配置行
 */
function add_forbidden_config(obj){
	var html = '';
	html += '<li class="ip">';
		html += '<span class="ip_fields">';
		html += '<input type="text" class="ip_field" />.';
		html += '<input type="text" class="ip_field" />.';
		html += '<input type="text" class="ip_field" />.';
		html += '<input type="text" class="ip_field" />';
		html += '</span>';
		html += '<a href="javascript:;" class="add_forbidden_between forbidden_action" onclick="add_forbidden_between(this);"> &gt; </a>';
		html += '<a href="javascript:;" class="drop_forbidden_between forbidden_action" onclick="drop_forbidden_between(this);" style="display:none;"> &lt; </a>';
		html += '<a href="javascript:;" class="add_forbidden_config forbidden_action" onclick="add_forbidden_config(this);" style="display:none;"> + </a>';
		html += '<a href="javascript:;" class="drop_forbidden_config forbidden_action" onclick="drop_forbidden_config(this);" > － </a>';
		html += '</li>';
	if($(obj).parent().parent().children().length<10){
		//$(obj).parent().after(html);
		$(obj).parent().parent().append(html);
	}else{
		msgbox("页面显示不下啦,不能再添加啦...")		
	}
}
/**
 * 删除过滤区间
 */
function drop_forbidden_between(obj){
	var current = $(obj);
	//新添加的span后段删除
	current.prev().remove();
	//显示后续按钮
	current.prev().css("display","inline");
	//自身隐藏
	current.css("display","none");
}
/**
 * 添加过滤区间
 */
function add_forbidden_between(obj){
	var current = $(obj);
	var html = '<span>&nbsp;-&nbsp;';
	html += '<input type="text" class="ip_field" />.';
	html += '<input type="text" class="ip_field" />.';
	html += '<input type="text" class="ip_field" />.';
	html += '<input type="text" class="ip_field" /></span>';
	var next = current.next();
	next.css("display","inline");
	current.after(html);
	current.css("display","none");
}

/**
 * 添加
 */
function add(obj){
	
	var current = $(obj);
	//过滤类型
	var current_forbidden = $(current.prev().prev().prev());
	//过滤的值
	var current_forbidden_value = $(current.prev().prev());
	//序号
	var current_order = $(current.prev().prev().prev().prev());
	//名称
	var current_name = $(current.prev().prev().prev().prev().prev());
	//所属操作面板 apps,regions,servers
	var owner = $(current.parent().parent().parent().parent());
	//操作面板的id
	var ownerId = owner.attr("id");
	//是否允许操作
	var canChange = true;
	//名称
	var name  = $.trim(current_name.val());
	//排序
	var order = $.trim(current_order.val());
	//过滤类型
	var forbidden_type = parseInt(current_forbidden.val());
	//过滤值
	var forbidden_value = current_forbidden_value.val();
	
	//名字是否异常
	if(name==""){
		msgbox("名称不能为空");
		return;
	}
	//序号是否异常
	if(isNaN(order)){
		msgbox("序号必须为数字");
		return;
	}
	
	$("#"+ownerId+" .display").each(function(){
		//是否有相同名称
		if($(this).html()==name){
			msgbox("已经存在该名称:"+name);
			current_name.focus();
			canChange = false;
			return false;
		}
	});
	if(canChange){
		
		//启用黑白名单
		/* if(parseInt(current_forbidden.val())==1){
			//1=白名单,2=黑名单
			forbidden_type = parseInt($.trim($("#forbidden_config input[name='forbidden_type']:checked").val()));
			console.log(forbidden_type);
		} */
		
		if(ownerId=="apps"){//添加应用
			//用户名
			var userName = $.trim($("#welcome_user").html());
			//判断用户名是否异常
			if(userName==""){
				msgbox("用户名异常");
				return;
			}
			$.get("/?method=addApp&args="+userName+"&args2="+name+"&args3="+order+"&args4="+forbidden_type+"&args5="+forbidden_value,function(data){
				data = eval("("+data+")");
				var code = data.code;
				if(msgcode(code)==1){
					var item_data = {
							name	:	name,
							id		:	data.id,
							order	:	order,
							forbiddenType	:	forbidden_type,
							forbiddenValue	:	forbidden_value
					}
					add_item_bydata($("#apps .loadinfo"),item_data);
					//add_item_bydetial($("#apps .loadinfo"),name,data.id,order,forbidden_type,forbidden_value);
					//current.parent().parent().after(html);
				}
			});
			
		}else if(ownerId=="regions"){//添加分区
			var parent_uuid = $("#place_app").attr("hash");
			if(null==parent_uuid||parent_uuid==""){
				msgbox("未选中应用");
				return;
			}
			$.get("/?method=addRegion&args="+parent_uuid+"&args2="+name+"&args3="+order+"&args4="+forbidden_type+"&args5="+forbidden_value,function(data){
				data = eval("("+data+")");
				var code = data.code;
				if(msgcode(code)==1){
					var item_data = {
							name	:	name,
							id		:	data.id,
							order	:	order,
							forbiddenType	:	forbidden_type,
							forbiddenValue	:	forbidden_value
					}
					add_item_bydata($("#regions .loadinfo"),item_data);
					//add_item_bydetial($("#regions .loadinfo"),name,data.id,order,forbidden_type,forbidden_value);
					//current.parent().parent().after(html);
				}
			});
		}else if(ownerId=="servers"){//添加服务器
			var parent_uuid = $("#place_region").attr("hash");
			if(null==parent_uuid||parent_uuid==""){
				msgbox("未选中分区");
				return;
			}
			//版本
			var version = $.trim(current_name.prev().val());
			
			if(version==""){
				msgbox("版本号不能空");
				return;
			}
			$.get("/?method=addServer&args="+parent_uuid+"&args2="+name+"&args3="+version+"&args4="+order+"&args5="+forbidden_type+"&args6="+forbidden_value,function(data){
				data = eval("("+data+")");
				var code = data.code;
				if(msgcode(code)==1){
					var item_data = {
							name	:	name,
							id		:	data.id,
							order	:	order,
							forbiddenType	:	forbidden_type,
							forbiddenValue	:	forbidden_value,
							version			:	version,
							status			:	101
					}
					add_item_bydata($("#servers .loadinfo"),item_data);
					//add_item_bydetial($("#servers .loadinfo"),name,data.id,order,forbidden_type,forbidden_value);
					//current.parent().parent().after(html);
				}
			});
		}
	}else if(ownerId=="process"){
		var parent_uuid = $("#place_server").attr("hash");
		if(null==parent_uuid||parent_uuid==""){
			msgbox("未选中服务器");
			return;
		}
		
	}
}

/**
 * 删除
 */
function drop(obj){
	var object = $(obj);
	var uuid = object.parent().prev().attr("id");
	var ownerId =  $(obj).parent().parent().parent().parent().attr("id");
	confirm("确定删除,以及子目录吗?",['确定','取消'],function(){
		var place_app = $("#place_app");
		var place_region = $("#place_region");
		var place_server = $("#place_server");
		if("apps"==ownerId){
			var place_uuid = place_app.attr("hash");
			var userName =$.trim($("#welcome_user").html());
			$.get("/?method=removeApp&args="+userName+"&args2="+uuid,function(data){
				data = eval("("+data+")");
				var code = data.code;
				if(1==msgcode(code)){
					if(uuid==place_uuid){
						//当前应用位置名称清空
						place_app.html("");
						//移除当前应用位置的hash值
						place_app.removeAttr("hash");
						//当前分区位置名称清空
						$("#place_region").html("");
						//移除当前分区位置的hash值
						$("#place_region").removeAttr("hash");
						//当前服务器位置名称清空
						$("#place_server").html("");
						//移除当前服务器位置的hash值
						$("#place_server").removeAttr("hash");
						//移除分区下的显示列表
						$("#regions ul li[class!='frist']").remove();
						//移除服务器下的显示列表
						$("#servers ul li[class!='frist']").remove();
						//移除进程下的显示列表
						$("#process ul li[lang!='keep']").remove();
					}
					object.parent().parent().remove();
				};
			});
		}else if("regions"==ownerId){
			var place_uuid = place_region.attr("hash");
			
			var appId = $.trim(place_app.attr("hash"));
			$.get("/?method=removeRegion&args="+appId+"&args2="+uuid,function(data){
				data = eval("("+data+")");
				var code = data.code;
				if(1==msgcode(code)){
					if(uuid==place_uuid){
						//当前分区位置名称清空
						$("#place_region").html("");
						//移除当前分区位置的hash值
						$("#place_region").removeAttr("hash");
						//当前服务器位置名称清空
						$("#place_server").html("");
						//移除当前服务器位置的hash值
						$("#place_server").removeAttr("hash");
						//移除服务器下的显示列表
						$("#servers ul li[class!='frist']").remove();
						//移除进程下的显示列表
						$("#process ul li[lang!='keep']").remove();
					}
					object.parent().parent().remove();
				};
			});
		}else if("servers"==ownerId){
			var place_uuid = place_server.attr("hash");
			var regionId = $.trim(place_region.attr("hash"));
			$.get("/?method=removeServer&args="+regionId+"&args2="+uuid,function(data){
				data = eval("("+data+")");
				var code = data.code;
				if(1==msgcode(code)){
					if(uuid==place_uuid){
						//当前服务器位置名称清空
						$("#place_server").html("");
						//移除当前服务器位置的hash值
						$("#place_server").removeAttr("hash");
						//移除进程下的显示列表
						$("#process ul li[lang!='keep']").remove();
					}
					object.parent().parent().remove();
				};
			});
		}else if("process"==ownerId){
			uuid = object.parent().parent().find(".uuid").html();
			var place_uuid = place_server.attr("hash");
			$.get("/?method=removeProcess&args="+place_uuid+"&args2="+uuid,function(data){
				data = eval("("+data+")");
				var code = data.code;
				if(1==msgcode(code)){
					object.parent().parent().remove();
				};
			});
		}
		//console.log("drop type:"+ownerId+" ,uuid:"+uuid);object.parent().parent().remove();
	},function(){}
	);
}
function confirm(message,btn,function_ok,function_canel){
	var i = $.layer({
	    shade : [0], //不显示遮罩
	    area : ['auto','auto'],
	    dialog : {
	        msg: message,
	        btns : 2, 
	        type : 4,
	        btn : btn,
	        yes : function(){
	        	function_ok();
	        	layer.close(i);
	        },
	        no : function(){
	        	function_canel();
	        	layer.close(i);
	        }
	    }
	});
}
/**
 * 更新
 */
function update(obj){
	
	var  object = $(obj);
	//过滤的类型
	var forbidden_type = $.trim(object.prev().prev().prev().val());
	//过滤的值
	var forbidden_value = $.trim(object.prev().prev().val());
	//排序
	var order = $.trim(object.prev().prev().prev().prev().val());
	//名称
	var name = $.trim(object.parent().prev().html());
	//id
	var uuid = $.trim(object.parent().prev().attr("id"));
	// 所属操作面板
	var ownerId = object.parent().parent().parent().parent().attr("id");
	
	if(name==""){
		msgbox("名称不能为空!");
		return;
	}
	if(uuid==""){
		msgbox("所属id异常!");
		return;
	}
	if(order==""){
		msgbox("序号不能为空!");
		return;
	}
	if(isNaN(order)){
		msgbox("序号为数字!");
		return;
	}
	if(ownerId==""){
		msgbox("所属面板异常!");
		return;
	}
	
	confirm("确定要更新该条配置吗?",['更新','取消'],function(){
		console.log("ownerId:"+ownerId+" ,uuid:"+uuid+" ,name:"+name+" ,order:"+order+" ,forbidden_type:"+forbidden_type+" ,forbidden_value:"+forbidden_value);
		if(ownerId=="apps"){
			var userName = $.trim($("#welcome_user").html());
			$.get("/?method=updateApp&args="+userName+"&args2="+uuid+"&args3="+name+"&args4="+order,function(data){
				data = eval("("+data+")");
				msgcode(data.code);
			});
		}else if(ownerId=="regions"){
			var parentId = $.trim($("#place_app").attr("hash"));
			$.get("/?method=updateRegion&args="+parentId+"&args2="+uuid+"&args3="+name+"&args4="+order+"&args5="+forbidden_type+"&args6="+forbidden_value,function(data){
				data = eval("("+data+")");
				msgcode(data.code);
			});
		}else if(ownerId=="servers"){
			//版本号
			var version = $.trim(object.prev().prev().prev().prev().prev().val());
			//状态
			var status = $.trim(object.prev().prev().prev().prev().prev().prev().val());
			
			if(version==""){
				msgbox("版本号不能为空!");
				return;
			}
			if(status==""){
				msgbox("请选择服务器状态!")
				;
				return;
			}
			var parentId = $.trim($("#place_region").attr("hash"));
			$.get("/?method=updateServer&args="+parentId+"&args2="+uuid+"&args3="+name+"&args4="+order+"&args5="+forbidden_type+"&args6="+forbidden_value+"&args7="+version+"&args8="+status,function(data){
				data = eval("("+data+")");
				msgcode(data.code);
			});
		}
	
	},function(){});
}
/**
 * 加载目录列表
 */
function list(obj){
	var object = $(obj);
	object.find("div").addClass("hightcolor");
	var app = $(object.children()[0]);
	var display = app.html();
	var uuid = app.attr("id");
	var owner = object.parent().parent();
	var ownerId = owner.attr("id");
	if("apps"==ownerId){
		$("#place_app").html(display);
		$("#place_app").attr("hash",uuid);
		var last = current_app_uuid;
		if(null!=last&&last!=uuid){
			$("#"+last).removeClass("hightcolor");			
			$("#"+last).next().removeClass("hightcolor");			
		}
		current_app_uuid = uuid; 
		load_regions(uuid);
	}else if("regions"==ownerId){
		$("#place_region").html(display);
		$("#place_region").attr("hash",uuid);
		var last = current_region_uuid;
		if(null!=last&&last!=uuid){
			$("#"+last).removeClass("hightcolor");		
			$("#"+last).next().removeClass("hightcolor");
		}
		current_region_uuid = uuid; 
		load_servers(uuid);
	}else if("servers"==ownerId){
		$("#place_server").html(display);
		$("#place_server").attr("hash",uuid);
		var last = current_server_uuid;
		if(null!=last&&last!=uuid){
			$("#"+last).removeClass("hightcolor");	
			$("#"+last).next().removeClass("hightcolor");
		}
		current_server_uuid = uuid; 
		load_processes(uuid);
	}
}

/**
 * 更新进程配置信息
 */
function update_process(obj){
	var object = $(obj);
	var online = $.trim(object.parent().prev().html());
	var usedMemory = $.trim(object.parent().prev().prev().html());
	var port = $.trim(object.parent().prev().prev().prev().html());
	var host = $.trim(object.parent().prev().prev().prev().prev().html());
	var order = $.trim(object.parent().prev().prev().prev().prev().prev().html());
	var uuid = $.trim(object.parent().prev().prev().prev().prev().prev().prev().html());
	//验证在线人数
	if(isNaN(online)||parseInt(online)<0){
		msgbox("在线人数格式有误");
		return;
	}
	//验证内存格式
	if(isNaN(usedMemory)||parseInt(usedMemory)<0){
		msgbox("内存格式有误");
		return;
	}
	//验证在线人数
	if(isNaN(port)||parseInt(port)<0){
		msgbox("端口格式有误");
		return;
	}
	//验证在线人数
	if(isNaN(order)||parseInt(order)<0){
		msgbox("序号格式有误");
		return;
	}
	if(uuid==""){
		msgbox("数据有错,请刷新页面");
		return;
	}
	var serverId = $("#place_server").attr("hash");
	if(serverId==""){
		msgbox("所属服务器异常!");
		return;
	}
	var jsonObject = JSON.stringify({
			order	:	order,
			host	:	host,
			port	:	port,
			usedMemory	:	usedMemory,
			online	:	online
	});
	
	var flag = true;
	$("#process ul li[lang!='keep']").each(function(){
		var li = $(this);
		var the_uuid = $.trim($(li.children()[0]).html());
		if(uuid==the_uuid){
			return true;
		}
		var the_host = $.trim($(li.children()[2]).html());
		var the_port = $.trim($(li.children()[3]).html());
		if(the_host==host&&the_port==port){
			msgbox("存在相同主机地址和端口");
			return flag &= false;
		}
	});
	if(flag){
		$.get("/?method=updateProcess&args="+serverId+"&args2="+uuid+"&args3="+jsonObject,function(data){
			data = eval("("+data+")");
			msgcode(data.code);
		});
	}
	console.log(jsonObject);
}

/**
 * 加载子进程列表
 */
function load_processes(id){
	if(null==id||id==""){
		msgbox("所属服务器id异常");
		return;
	}
	$("#process ul li[lang!='keep']").remove();
	$.get("/?method=loadProcesses&args="+id,function(data){
			data = eval("("+data+")");
			var list =data;
			var html = "";
			for(var index in list){
				var item = list[index];
				var order = item.order;
				var host = 	item.host;		
				var port = 	item.port;		
				var usedMemory = item.usedMemory;		
				var online = item.online;		
				var uuid = item.id;
				html += "<li class='process_item' onmouseover='seeOver(this)' onmouseout='seeOut(this)'>";
				html += '<div class="notice uuid">'+uuid+'</div>';
				html += '<div class="notice process" contenteditable="true">'+order+'</div>';
				html += '<div class="notice process" contenteditable="true">'+host+'</div>';
				html += '<div class="notice process" contenteditable="true">'+port+'</div>';
				html += '<div class="notice process" contenteditable="true">'+usedMemory+'</div>';
				html += '<div class="notice process" contenteditable="true">'+online+'</div>';
				html += '<div class="notice process act">';
				html += '<button onclick="update_process(this);" class="action_btn">更新</button>';
				html += '<button onclick="connect(this);" class="action_btn">连接</button>';
				html += '<button onclick="drop(this);" class="action_btn">删除</button>';
				html += '</div>';
				html += '</li>';
			}
			$("#process .loadinfo").append(html);
			resert_process();
	});
	
	if(parseInt(Math.random()*10)>5){
	
	}
}
 

/**
 * 重置进程输入框
 */
function resert_process(){
	$("#pOrder").html("");
	$("#host").html("");
	$("#port").html("");
	$("#usedMemory").html("");
	$("#online").html("");
}
/**
 * 提示
 */
function msgbox(message,type){
	//alert(message);
	if(!(arguments[1])){
		type = 9;
	}
	layer.alert(message,type);
}

/**
 * 登陆
 */
function login(){
	var name = $.trim($("#user_name").val());
	var password = $.trim($("#user_password").val());
	if(name==""){
		msgbox("用户名不能为空");
		return;
	}
	if(password==""){
		msgbox("密码不能为空");
		return;
	}
	$.get("/?method=loadUser&args="+name+"&args2="+password,function(data){
		data = eval("("+data+")");
		var code = data.code;
		if(code==1){
			$("#userPannel").css("display","none");
			$("#welcome_user").html(name);
			$("#status").css("display","block");
			$("#main").css("display","block");
			load_app();
		}else{
			msgcode(code);
		}
	});
}


/**
 * 弹出code
 */
function msgcode(code){
	if(code==1){
		msgbox("成功");
	}else if(code==2){
		msgbox("失败");
	}else if(code==1001){
		msgbox("应用名称已经存在");
	}else if(code==1002){
		msgbox("应用名称不存在");
	}else if(code==1003){
		msgbox("用户已经存在");
	}else if(code==1004){
		msgbox("用户不存在");
	}else if(code==1005){
		msgbox("分区已经存在");
	}else if(code==1006){
		msgbox("分区不存在");
	}else if(code==1007){
		msgbox("服务器存在");
	}else if(code==1008){
		msgbox("服务器不存在");
	}else if(code==1009){
		msgbox("存在进程");
	}else if(code==1010){
		msgbox("不存在进程");
	}else if(code==1011){
		msgbox("密码错误");
	}else if(code==1012){
		msgbox("名称已经存在");
	}else if(code==1013){
		msgbox("存在相同主机地址和端口");
	}else{
		msgbox("unknow code:"+code);
	}	
	console.log(code);
	return code;
}

/**
 * 注册
 */
function register(){
	 var name = $.trim($("#user_name").val());
		var password = $.trim($("#user_password").val());
		if(name==""){
			msgbox("用户名不能为空");
			return;
		}
		if(password==""){
			msgbox("密码不能为空");
			return;
		}
		$.get("/?method=addUser&args="+name+"&args2="+password,function(data){
			data = eval("("+data+")");
			var code = data.code;
			msgcode(code);
		});
}
/**
 * 添加应用
 */
function add_app(){
	//新添加的应用名称
	var newAppName = $.trim($("#newAppName").val());
	//用户名
	var userName = $.trim($("#welcome_user").html());
	
	if(userName==""){
		msgbox("用户名异常");
		return;
	}
	if(newAppName==""){
		msgbox("应用名称不能为空");
		return;
	}
	$.get("/?method=addApp&args="+userName+"&args2="+newAppName,function(data){
		data = eval("("+data+")");
		var code = data.code;
		msgcode(code);
		if(code==1){
			var newAppHtml = "<a class='app' herf='#' onclick='load_regions(this);' id='"+data.id+"' title='"+newAppName+"'>"+newAppName+"</a>";
			$("#apps").append(newAppHtml);
		}
	});
}

/**
 * 添加分区
 */
function add_region(){
	var fromAppId = $.trim($("#fromAppId").val());
	var newRegionName = $.trim($("#newRegionName").val());
	
	if(fromAppId==""){
		msgbox("应用来源异常");
		return;
	}
	if(newRegionName==""){
		msgbox("索要添加的分区名称不能为空");
		return;
	}
	$.get("/?method=addRegion&args="+fromAppId+"&args2="+newRegionName,function(data){
		data = eval("("+data+")");
		var code = data.code;
		msgcode(code);
		if(code==1){
			var id = data.id;
			var server_item_html = "<a class='region' herf='#' onclick='load_servers(this);' id='"+id+"'>"+newRegionName+"</a>\n";
			$("#regions").append(server_item_html);
		}
	});
}
/**
 * 添加新服务器
 */
function add_server(){
	var fromId = $.trim($("#fromRegionId").val());
	var newName = $.trim($("#newServerName").val());
	
	if(fromId==""){
		msgbox("分区来源异常");
		return;
	}
	if(newName==""){
		msgbox("索要添加的服务器名称不能为空");
		return;
	}
	$.get("/?method=addServer&args="+fromId+"&args2="+newName+"&args3=v1.0.0",function(data){
		data = eval("("+data+")");
		var code = data.code;
		msgcode(code);
		if(code==1){
			var id = data.id;
			var server_item_html = "<a class='server' herf='#' onclick='load_processes(this);' id='"+id+"'>"+newName+"</a>\n";
			$("#servers").append(server_item_html);
		}
	});
}


/**
 * 浏览
 */
function browse(obj){
	//获取当前浏览对象的uuid
	var  uuid = $(obj).parent().prev().attr("id");
	//所属面板
	var ownerId = $(obj).parent().parent().parent().parent().attr("id");
	
	var url  = null;
	
	if(ownerId=="apps"){
		
	}else if(ownerId=="regions"){
		url = "/?method=loadByRegion&args="+uuid;
	}else if(ownerId=="servers"){
		url = "/?method=loadByServer&args="+uuid;
	}
	if(null!=url){
		console.log(url);
		window.open(url);
	}
	
}


/**
 * 鼠标进入
 */
function seeOver(obj){
	$(obj).find("div").addClass("hg");
}

/**
 * 鼠标离开
 */
function seeOut(obj){
	$(obj).find("div").removeClass("hg");
}

/**
 * 添加应用项目
 */
function add_item_bydata(target,data){
	var name = data.name;
	var id = data.id;
	var order = data.order;
	var forbidden_type = parseInt(data.forbiddenType);
	var forbidden_value = data.forbiddenValue;

	var forbidden_label = null;
	//过滤类型
	if(forbidden_type==1){
		forbidden_label = "白名单";
	}else if(forbidden_type==2){
		forbidden_label = "黑名单";
	}else{
		forbidden_label = "不过滤";
	}
	var html = "";
	html += "<li onclick='list(this)' onmouseover='seeOver(this)' onmouseout='seeOut(this)'>";
	html += '<div class="notice display" contenteditable="true" id="'+id+'" onblur="change_display(this);" title="'+name+'">'+name+'</div>';
	html += '<div class="notice action">';
	if(data.status){
		var status = parseInt(data.status);
		html += '<select class="select server_status">';
		html += status==101?'<option value="101" selected="selected">正常</option>':'<option value="101">正常</option>';
		html += status==102?'<option value="102" selected="selected">拥挤</option>':'<option value="102">拥挤</option>';
		html += status==103?'<option value="103" selected="selected">爆满</option>':'<option value="103">爆满</option>';
		html += status==104?'<option value="104" selected="selected">维护</option>':'<option value="104">维护</option>';
		html += status==105?'<option value="105" selected="selected">停止</option>':'<option value="105">停止</option>';
		html += '</select>'; 
	}
	if(data.version){
		html += '<input type="text" class="version" value="'+data.version+'" />';
	}
	html += '<input type="text" class="order" value="'+order+'">';
	html += '<input type="hidden" class="forbidden_type" value="'+forbidden_type+'" />';
	html += '<input type="hidden" class="forbidden_value" value="'+forbidden_value+'" />';
	if(target.parent().attr("id")=="apps"){
		html += '<button onclick="edit_forbidden(this);" class="action_btn" style="display:none;">';
	}else{
		html += '<button onclick="edit_forbidden(this);" class="action_btn">';
	}
	html += forbidden_label;
	html += '</button>';
	//html += '<button onclick="list(this);" class="action_btn">选择</button>';
	html += '<button title="更新" onclick="update(this);" class="update">更新</button>';
	html += '<button title="删除" onclick="drop(this);" class="drop">删除</button>';
	html += '<button title="浏览" onclick="browse(this);" class="browse">浏览</button>';
	html += '</div>';
	html += "</li>";
	target.append(html);
	
	
}
/**
 * 加载app相关信息
 */
function load_app(){
	//用户名
	var userName = $.trim($("#welcome_user").html());

	if(userName==""){
		msgbox("用户名异常");
		return;
	}
	$.get("/?method=loadApps&args="+userName,function(data){
		data = eval("("+data+")");
		var list = data;
			$("#regions ul li[class!='frist']").remove();
			$("#servers ul li[class!='frist']").remove();
			$("#process ul li[lang!='keep']").remove();
			for(var index in list){
				var app = list[index];
				add_item_bydata($("#apps .loadinfo"),app);
			}
	});
}

/**
 * 加载分区列表
 */
function load_regions(appId){
	if(null==appId||appId==""){
		msgbox("所属应用id异常");
		return;
	}
	$.get("/?method=loadRegions&args="+appId,function(data){
		data = eval("("+data+")");
		var list = data;
			$("#regions ul li[class!='frist']").remove();
			$("#servers ul li[class!='frist']").remove();
			$("#process ul li[lang!='keep']").remove();
			$("#place_server").html("");
			$("#place_server").removeAttr("hash");
			if(list.length==0){
				$("#place_region").html("");
				$("#place_region").removeAttr("hash");
				
			}else{
				for(var index in list){
					var item = list[index];
					add_item_bydata($("#regions .loadinfo"),item);
				}
			}
	});
}
/**
 * 加载分区列表
 */
function load_servers(id){
	if(null==id||id==""){
		msgbox("所属分区id异常");
		return;
	}
	$.get("/?method=loadServers&args="+id,function(data){
		data = eval("("+data+")");
			var list = data;
			$("#servers ul li[class!='frist']").remove();
			$("#process ul li[lang!='keep']").remove();
			if(list.length==0){
				$("#place_server").html("");
				$("#place_server").removeAttr("hash");
			}else{
				for(var index in list){
					var item = list[index];
					add_item_bydata($("#servers .loadinfo"),item);
				}
			}
	});
}


/**
 * 编辑名称
 */
function editName(type,obj){
	var object = $(obj);
	var hash = object.attr("hash");
	var newName = object.val();
	var beforeName = object.attr("value");
	if(newName!=""){
		console.log("edit type:"+type+" ,hash:"+hash+" ,newName:"+newName+", beforeName:"+beforeName);
		var id =  type=="1"?beforeName:hash;
		console.log(id);
		$.get("/?method=changeName&args="+type+"&args2="+id+"&args3="+newName,function(data){
			data = eval("("+data+")");
			var code = data.code;
			if(code==1){
				console.log("更新成功");
			}else{
				msgbox("名称已经存在");
				object.val(beforeName);
			}
		});
	}
}

/**
 * 添加进程信息
 */
function add_process(){
	
	var parent_uuid = $.trim($("#place_server").attr("hash"));
	
	if(parent_uuid==""){
		msgbox("为选择服务器");
		return;
	}
	var order = $.trim($("#pOrder").html());
	//主机地址
	var host = $.trim($("#host").html());
	//主机端口
	var port = $.trim($("#port").html());
	//使用内存
	var usedMemory = $.trim($("#usedMemory").html());
	//在线人数
	var online = $.trim($("#online").html());
	
	if(order==""){
		msgbox("序号不能为空");
		return;
	}
	if(isNaN(order)){
		msgbox("序号格式有误");
		return;
	}
	if(host==""){
		msgbox("主机地址不能为空");
		return;
	}
	var reg = /^([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])\.([0,1]?\d{0,2}|2[0-4]\d|25[0-5])$/;  
	if(!reg.test(host)){
		msgbox("主机地址格式异常");
		return;
	} 
	if(port==""){
		msgbox("主机端口不能为空");
		return;
	}
	if(isNaN(port)){
		msgbox("主机端口为数字");
		return;
	}else{
		port  = parseInt(port);
		if(port<=0||port>65535){
			msgbox("正确端口号为 1 - 65535 之间 !");
			return;
		}
	}
	if(usedMemory==""){
		msgbox("内存使用情况不能为空");
		return;
	}
	if(isNaN(usedMemory)){
		msgbox("内存使用情况为数字");
		return;
	}
	if(online==""){
		msgbox("在线人数不能为空");
		return;
	}
	if(isNaN(online)){
		msgbox("在线人数为数字");
		return;
	}
	
	var success = true;
	$("#process .process_item").each(function(){
		var process_item = $(this);
		
		var childrens = process_item.children();
		var children_order = childrens[0];
		var children_host = childrens[1];
		var children_port = childrens[2];
		var children_usedMemory = childrens[3];
		var children_online = childrens[4];
		
		var _host = $(children_host).html();
		var _port = $(children_port).html();
		//已经存在相关主机地址和端口
		if(host==_host&&port==_port){
			msgbox("相同主机地址:"+_host+" ,主机端口:"+_port);
			return success &= false;
		}
	});
	
	if(success){
		$.get("/?method=addProcess&args="+parent_uuid+"&args2=&args3="+order+"&args4="+host+"&args5="+port+"&args6="+usedMemory+"&args7="+online,function(data){
			data = eval("("+data+")");
			if(msgcode(data.code)==1){
				var uuid = data.id;
				var html = "";
				html += "<li class='process_item' onmouseover='seeOver(this)' onmouseout='seeOut(this)'>";
				html += '<div class="notice uuid">'+uuid+'</div>';
				html += '<div class="notice process" contenteditable="true">'+order+'</div>';
				html += '<div class="notice process" contenteditable="true">'+host+'</div>';
				html += '<div class="notice process" contenteditable="true">'+port+'</div>';
				html += '<div class="notice process" contenteditable="true">'+usedMemory+'</div>';
				html += '<div class="notice process" contenteditable="true">'+online+'</div>';
				html += '<div class="notice process act">';
				html += '<button onclick="update_process(this);" class="action_btn">更新</button>';
				html += '<button onclick="connect(this);" class="action_btn">连接</button>';
				html += '<button onclick="drop(this);" class="action_btn">删除</button>';
				html += '</div>';
				html += '</li>';
				$("#process .loadinfo").append(html);
				resert_process();
			}
		});
	}
}

/**
 *  连接主机,端口
 */
function connect(obj){
	
	var object = $(obj);
	//主机地址
	var host = $.trim($(object.parent().prev().prev().prev().prev()).html());;
	//主机端口
	var port = $.trim($(object.parent().prev().prev().prev()).html());
	
	$.get("/?method=connect&args="+host+"&args2="+port,function(data){
		data = eval("("+data+")");
		var code = data.code;
		if(code==1){
			msgbox(data.msg);
		}
	});
	
}
 
/**
 * 显示子面板
 */
function show(obj){
	//获取显示超链
	var object = $(obj);
	//获取隐藏超链
	var hidden = object.next();
	//隐藏自己
	object.addClass("hide").removeClass("show");;
	//将隐藏按钮显示出来
	hidden.addClass("show").removeClass("hide");;
	
	var ul = object.parent().parent().parent().parent();
	
	$(ul).find("li[class!='frist']").css("display","block");
}

/**
 * 隐藏子面板
 */
function hide(obj){
	//获取隐藏的超链
	var object = $(obj);
	//获取显示的超链
	var show = object.prev();
	//隐藏自己
	object.addClass("hide").removeClass("show");
	//将显示按钮显示出来
	show.addClass("show").removeClass("hide");
	
	var ul = object.parent().parent().parent().parent();
	
	$(ul).find("li[class!='frist']").css("display","none");
}

/**
 * 刷新
 */
function refresh(obj){
	msbox("此功能暂时未开放!")	
}