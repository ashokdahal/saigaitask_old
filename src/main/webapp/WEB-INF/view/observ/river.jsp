<%/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
%>
<%@include file="../common/lang_resource.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>${f:h(siteName) }</title>
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-ui-1.10.3/themes/base/jquery-ui.css')}" />
<link rel="stylesheet" type="text/css" href="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.css" />
<script type="text/javascript" src="${f:url('/js/jquery-1.10.2.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-ui-1.10.3/ui/minified/jquery-ui.min.js')}"></script>
<script type="text/javascript" src="${f:url('/js/jquery-loadmask-0.4')}/jquery.loadmask.min.js"></script>
<link rel="stylesheet" type="text/css" href="${f:url('/css/btn.css')}" />
<script type="text/javascript" src="${f:url('/js/btn.js')}"></script>
<style type="text/css">
@media print {
	div.footer {
		display: none !important;
	}
}
</style>
<style type="text/css">
<%-- とりあえずコメントアウト...
html {
 display: table;
 width: 100%;
 height: 100%;
}
body {
 display: table-cell;
 vertical-align: middle;
	background-color: #444;
}
--%>
table {
	background-color: #ffffff;
	/*
	border-top:#ffffff 3px double;
	*/
	border-collapse: collapse;
	/*
	font-size: 11px;
	width: 50%;
	color:#333333;
	margin-left: auto;
	margin-right: auto;
	*/
}
table#barometer td.arrow {
	background-color: #FFFF99;
	text-align: center;
	font-weight: bold;
}
table#barometer td.noarrow {
	background-color: #FFFFFF;
	text-align: center;
	font-weight: bold;
}

table.list td {
	text-align: right;
	padding: 0px 4px;
}
<%--
table th.t_top {
	border: #dcdddd 1px solid;
	background-color: #efefef;
	text-align: left;
	padding: 10px;
}
tr:nth-child(even) td {
	border: #dcdddd 1px solid;
	background-color: #fff;
	text-align: left;
	padding: 10px;
	vertical-align: top;
}
tr:nth-child(odd) td {
	border: #dcdddd 1px solid;
	background-color: #f7f8f8;
	text-align: left;
	padding: 10px;
	vertical-align: top;
}
...とりあえずコメントアウト --%>
th.head {
	background-color: #E6E6E6;
}
</style>
<script type="text/javascript">
//for IE
{
	var console = {
		log : function() {},
		debug : function() {},
		info : function() {},
		warn : function() {},
		error : function() {},
		assert : function() {},
		trace : function() {}
	};

	if (typeof window.console == "undefined") {
		window.console = console;
	}
	else {
		for ( var idx in console) {
			if (window.console[idx] == "undefined") {
				window.console[idx] = console[idx];
			}
		}
	}
}

//自動ページリロード
$(document).ready(function() {
	setTimeout(function(){ console.log("Done!");location.reload();}, 120000);
});

//プリントダイアログを表示
$(document).ready(function(){
	$('span.print_btn').click(function(){
		window.print();
		return false;
	});
});
</script>
</head>
<body >

<c:set var="river" value="${observatoryriverInfo}"/>
<div class="container" style="width:900px; /*height:600px;*/ margin:0 auto;">
	<div class="header" style="width:900px; height:45px; background-color:#000000; color:#BEF1FF; display:block; line-height:45px;">
		<span style="font-weight:bold; margin:4px;"><%=lang.__("Telemeter")%>&nbsp;<%=lang.__("Water level")%>&nbsp;&nbsp;${f:h(river.name)}（${f:h(river.readname)}）</span>
	</div><!-- end .header -->

	<div class="content">
		<table width="900" border="0">
			<tr>
				<td style="text-align:right;"><%=lang.__("Observation time")%>：${f:h(observdate)}</td>
			</tr>
			<tr>
				<td width="" style="text-align:right;">
<c:choose>
	<c:when test="${timeaxis==Observ.MIN}">
					<a href="${f:url('/observ/river')}/${id}/${Observ.HOUR}?time=${time}"><%=lang.__("On the hour every hour")%></a>&nbsp;/&nbsp;<span style="font-weight:bold"><%=lang.__("10 minutes each")%></span>
	</c:when>
	<c:otherwise>
					<span style="font-weight:bold;"><%=lang.__("On the hour every hour")%></span>&nbsp;/&nbsp;<a href="${f:url('/observ/river')}/${id}/${Observ.MIN}?time=${time}"><%=lang.__("10 minutes each")%></a>
	</c:otherwise>
</c:choose>
				</td>
			</tr>
		</table>
		<table width="900" border="0">
			<tr>
				<td colspan="2">
					<!-- 観測地点情報 -->
					<table border="1" style="font-size:80%; text-align:center;" id="summary">
						<caption></caption>
						<tr>
							<th width="80" class="head"><%=lang.__("The name of the water system")%></th>
							<th width="120" class="head"><%=lang.__("River name")%></th>
							<th width="100" class="head"><%=lang.__("Observation name")%></th>
							<!-- <td width="60" class="head">管理区分</th> -->
							<th width="140" class="head"><%=lang.__("Jurisdiction")%></th>
							<!-- <th width="80" class="head"><%=lang.__("Position")%></th>-->
							<th width="220" class="head"><%=lang.__("Location")%></th>
							<th width="160" class="head"><%=lang.__("Zero point high")%></th>
						</tr>
						<tr>
							<td>${f:h(river.basin) }</td>
							<td>${f:h(river.river) }</td>
							<td>${f:h(river.name) }</td>
							<!-- <td rowspan="2">{国河川|自治体|etc.}</td> -->
							<td>${f:h(river.officename) }</td>
							<!-- <td>{右岸|左岸}</td>-->
							<td>${f:h(river.address) }</td>
							<td><%=lang.__("Altitude")%><fmt:formatNumber value="${f:h(river.altitude) }" pattern="0.0000" />m</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td width="300" style="vertical-align:top;">
					<br>
					<table border="1" style="font-size:80%" class="list">
						<caption></caption>
						<tr>
							<th colspan="2" class="head"><%=lang.__("Times of day")%></th>
							<th colspan="2" width="100" class="head"><%=lang.__("Water level (m)")%></th>
						</tr>
						<tbody id="realdata">
<c:set var="pre_date" value=""/>
<c:forEach var="e" varStatus="s" items="${tableItems}">
	<c:set var="dateParts" value="${fn:split(e.datelabel, '/')}" />
	<c:set var="md" value="${dateParts[1]}/${dateParts[2]}" />
						<tr>
							<td style="border-style:none none none solid; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'};">${pre_date!=e.datelabel?md:'&nbsp;'}</td>
							<td style="border-style:none solid none none; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'};">${f:h(e.timelabel)}</td>
							<td style="border-style:none none none solid; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'};" width="70px">${(empty e.contentscode1)?'N/A':(e.contentscode1!=0)?e.contentsname1:e.itemdata1}</td>
							<td style="border-style:none solid none none; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'}; text-align:center;">${(e.itemdataarrow1) }</td>
						</tr>
	<c:set var="pre_date" value="${e.datelabel}"/>
</c:forEach>
						</tbody>
					</table>
				</td>
				<td width="600">
					<table width="600" border="0">
						<tr>
							<td>
								<br>
								<table id="barometer" border="1" style="font-size:80%">
									<tr>
										<th class="head" ><%=lang.__("Item")%></th>
										<th colspan="5" class="head"></th>
									</tr>
									<tr>
										<th width="55" nowrap class="head"><%=lang.__("Standard level")%></th>
										<td colspan="6">
											<table width="100%">
												<tr>
													<td width="8%">&nbsp;</td>
													<td width="18%"><div align="center"><font color="#0000CC"><%=lang.__("Flood prevention team waiting")%><br><%=lang.__("Water level")%><br>${(!empty river.waterlevel1)?f:h(river.waterlevel1):'-' }&nbsp;m</font></div></td>
													<td width="14%"><div align="center"><font color="#FF6600"><%=lang.__("Flooding caution")%><br><%=lang.__("Water level")%><br>${(!empty river.waterlevel2)?f:h(river.waterlevel2):'-' }&nbsp;m</font></div></td>
													<td width="16%"><div align="center"><font color="#FF0000"><%=lang.__("Shelter judgment")%><br><%=lang.__("Water level")%><br>${(!empty river.waterlevel3)?f:h(river.waterlevel3):'-' }&nbsp;m</font></div></td>
													<td width="16%"><div align="center"><font color="#FF00FF"><%=lang.__("In danger of flooding")%><br><%=lang.__("Water level")%><br>${(!empty river.waterlevel4)?f:h(river.waterlevel4):'-' }&nbsp;m</font></div></td>
													<td width="6%">&nbsp;</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<th width="10%" class="head"><%=lang.__("Water level")%></th>
										<td width="18%" class="${waterlevelIdx>=0?"arrow":"noarrow" }"><c:if test="${waterlevelIdx==0 }">${lastdata }&nbsp;m&nbsp;${lastarrow }</c:if></td>
										<td width="18%" class="${waterlevelIdx>=1?"arrow":"noarrow" }"><c:if test="${waterlevelIdx==1 }">${lastdata }&nbsp;m&nbsp;${lastarrow }</c:if></td>
										<td width="18%" class="${waterlevelIdx>=2?"arrow":"noarrow" }"><c:if test="${waterlevelIdx==2 }">${lastdata }&nbsp;m&nbsp;${lastarrow }</c:if></td>
										<td width="18%" class="${waterlevelIdx>=3?"arrow":"noarrow" }"><c:if test="${waterlevelIdx==3 }">${lastdata }&nbsp;m&nbsp;${lastarrow }</c:if></td>
										<td width="18%" class="${waterlevelIdx>=4?"arrow":"noarrow" }"><c:if test="${waterlevelIdx==4 }">${lastdata }&nbsp;m&nbsp;${lastarrow }</c:if></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td width="600">
								<div id="riverChart_div" style="width:600px; height:350px"></div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div><!-- end .content -->

	<div class="footer">
		<table width="900" border="0">
			<tr>
				<td style="text-align:right;">
					<div>
						<ul class="buttons">
							<li><a class="btn blue" href="#" onClick="${river.iframe?'self.parent.tb_remove();':'window.close();'} return false;"><%=lang.__("Close")%></a></li>
							<li><a class="btn blue" href="javascript:print()"><%=lang.__("Print")%></a></li>
						</ul>
					</div>
				</td>
			</tr>
		</table>
	</div><!-- end .footer -->

</div><!-- end .container -->

</body>


<!--Load the AJAX API-->
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">

	// Load the Visualization API and the piechart package.
	google.charts.load('visualization', '1.0', { 'packages':['corechart'], 'language':'ja' });

<c:if test="${fn:length(graphItems) > 0 }">
	// Set a callback to run when the Google Visualization API is loaded.
	google.charts.setOnLoadCallback(drawChart);

	// Callback that creates and populates a data table,
	// instantiates the pie chart, passes in the data and
	// draws it.
	function drawChart() {

		//データテーブルの作成
		var data = new google.visualization.DataTable();

		//カラムの宣言
		data.addColumn({type:'datetime', role:'domain', label:'<%=lang.__("Times of day")%>'});
		data.addColumn({type:'number',   role:'data',   label:'<%=lang.__("In danger of flooding (m)")%>'});
		data.addColumn({type:'number',   role:'data',   label:'<%=lang.__("Shelter judgment (m)")%>'});
		data.addColumn({type:'number',   role:'data',   label:'<%=lang.__("Flooding caution (m)")%>'});
		data.addColumn({type:'number',   role:'data',   label:'<%=lang.__("Flood prevention team waiting(m)")%>'});
		data.addColumn({type:'number',   role:'data',   label:'<%=lang.__("Water level (m)<!--2-->")%>'});

		//レコードの追加([datetime, num, num])<%-- 累加雨量が0の場合は、グラフを描画しないようにするため値をnullにする. --%>
		data.addRows([
<c:forEach var="e" varStatus="s" items="${graphItems}">
					[{v:new Date('${e.datevalue } ${e.timevalue }'), f:'${e.datelabel } ${e.timelabel }'}, ${(!empty river.waterlevel4)?f:h(river.waterlevel4):null }, ${(!empty river.waterlevel3)?f:h(river.waterlevel3):null }, ${(!empty river.waterlevel2)?f:h(river.waterlevel2):null }, ${(!empty river.waterlevel1)?f:h(river.waterlevel1):null }, {v:${(e.contentscode1==0)?e.itemdata1:'null' }, f:'${(e.contentscode1==0)?e.itemdata1:e.contentsname1 }' } ]${s.last?"":"," }
</c:forEach>
		]);
		<%-- 警戒値の線を描画するため、値を追加する. --%>
		var d = new Date('${graphItems[fn:length(graphItems)-1].datevalue } ${graphItems[fn:length(graphItems)-1].timevalue }');
<c:forEach var="i" varStatus="s" begin="1" end="24">
		data.addRow( [ new Date(d.setHours(d.getHours() + 1)), ${(!empty river.waterlevel4)?f:h(river.waterlevel4):null }, ${(!empty river.waterlevel3)?f:h(river.waterlevel3):null }, ${(!empty river.waterlevel2)?f:h(river.waterlevel2):null }, ${(!empty river.waterlevel1)?f:h(river.waterlevel1):null }, null ] );
</c:forEach>

//		//フォーマッターの作成
//		var formatter_dec2 = new google.visualization.NumberFormat({fractionDigits: 2});			//小数点一位を表示
//		var formatter_ymd6HHmm = new google.visualization.DateFormat({pattern: "yyyy/MM/dd HH:mm"});	//日付を24時間表記  eg, 23:59 + 1 hour =>> 00:59
//		var formatter_ymd6kkmm = new google.visualization.DateFormat({pattern: "yyyy/MM/dd kk:mm"});	//日付を24時間表記  eg, 23:59 + 1 hour =>> 24:59
//
//		//データの整形
//		formatter_ymd6HHmm.format(data, 0);	//日付をフォーマット		<%-- 24:00を正しく表記できないため、こちらにしています. --%>
//		//formatter_ymd6kkmm.format(data, 0);	//日付をフォーマット
//		formatter_dec2.format(data, 1);		//フォーマットをdataのフィールド１に適用
//		formatter_dec2.format(data, 2);		//フォーマットをdataのフィールド２に適用

		//グラフのプロット期間
		var currentDatetime = new Date('${graphItems[fn:length(graphItems)-1].datevalue } ${graphItems[fn:length(graphItems)-1].timevalue }');
		var minDatetime = new Date('${graphItems[0].datevalue } ${graphItems[0].timevalue }');
		var maxDatetime = new Date('${graphItems[fn:length(graphItems)-1].datevalue } ${graphItems[fn:length(graphItems)-1].timevalue }');
		maxDatetime.setDate(maxDatetime.getDate() + 1);	//未来を１日追加


		//グラフのY軸
		var minValueY1 = 'automatic';		//グラフ要素の最小値
		var minValueY2 = 'automatic';		//グラフ要素の最小値
		var maxValueY1 = 'automatic';		//グラフ要素の最大値
		var maxValueY2 = 'automatic';		//グラフ要素の最大値
		var gridlineCnt = 'automatic';	//補助線の数
		<c:if test="${(!empty river.levelmin) }">levelmin = ${river.levelmin }</c:if>
		<c:if test="${(!empty river.levelmax) }">levelmax = ${river.levelmax }</c:if>
		<c:if test="${(!empty river.levelmin) && (!empty river.levelmax) }">
		var range = maxValueY1 - minValueY1;
		switch (range) {
			case 0.5:
			case 5:
			case 10:
				gridlineCnt = 6;
				break;
			case 12.5:
				gridlineCnt = 11;
				break;
			case 15:
				gridlineCnt = 16;
				break;
			case 20:
				gridlineCnt = 11;
				break;
		}
		</c:if>

		//グラフ仕様
		//  X軸  （時刻）	：datetime型
		//  Y軸１（水位）	：lineチャート
		//グラフオプションの設定
		var options = {
				//基本設定
				width: 550, height: 350,			//グラフサイズ
				backgroundColor: { fill: 'white', stroke: 'black', strokeWidth: 0 },	//グラフエリア（背景色、枠線色、枠線幅）
				//chartArea: { left: 'auto', top: 'auto', width: '61.8%', height: '61.8%' },		//プロットエリア
				fontSize: 11,
				fontName: 'Arial',				//default: 'Arial'
				ebableInteractivity: true,		//イベント動作

				//ツールチップ
				focusTarget: 'datum',			//ツールチップ内容 {'datum'|'category'}
				tooltip: {
					trigger: true,				//ツールチップを表示
					showColorCode: true,		//ツールチップに凡例を表示
					textStyle: { /* color: 'black', */ /* fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
				},

				//タイトル
				title: '',
				titlePosition: 'none',
				titleTextStyle: { /* color: 'black', */ /*fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },

				//凡例
				legend: {
					position: 'right',			//表示位置
					textStyle: { /* color: 'black', */ /* fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
					alignment: 'center'			//文字寄せ位置
				},

				//複合グラフタイプの指定
				series: {
					0: { //氾濫危険
						type: 'line',
						color: '#FF00FF',			//グラフの色
						targetAxisIndex: 0,			//vAxis設定の指定
						PointSize: 0,				//グラフの点,   Available: 'line',
						lineWidth: 1,				//グラフの線幅, Available: 'line',
						visibleInLegend: true,		//凡例の表示
						enableInteractivity: false	//イベント動作
					},
					1: { //避難所判断
						type: 'line',
						color: '#FF0000',
						targetAxisIndex: 0,
						PointSize: 0,
						lineWidth: 1,
						visibleInLegend: true,
						enableInteractivity: false
					},
					2: { //氾濫注意
						type: 'line',
						color: '#FF6600',
						targetAxisIndex: 0,
						PointSize: 0,
						lineWidth: 1,
						visibleInLegend: true,
						enableInteractivity: false
					},
					3: { //水防団待機
						type: 'line',
						color: '#0000FF',
						targetAxisIndex: 0,
						PointSize: 0,
						lineWidth: 1,
						visibleInLegend: true,
						enableInteractivity: false
					},
					4: { //水位
						type: 'line',
						color: '#00FFFF',
						targetAxisIndex: 0,
						PointSize: 0,
						lineWidth: 2,
						visibleInLegend: true
					}
				},
				//縦軸
				axisTitlesPosition: 'out',		//縦軸のタイトルの位置
				vAxes: {
					0: { //左軸用(line)
						title: '<%=lang.__("Water level (m)")%>',
						//データ範囲
						viewWindowMode: 'pretty',		//{'pretty'|'maximized'|'explicit'}
						//目盛
						format: '0.0',
						//目盛線
						gridlines:      { color: '#b4b4b4', count: gridlineCnt },	//目盛線
						minorGridlines: { color: '#f0f0f0', count: 1 },	//補助目盛線
						minValue: levelmin,
						maxValue: levelmax
					}
				},
				//横軸
				hAxis: {
					//横軸タイトル
					//title: '時刻',
					titleTextStyle: { /* color: 'black', */ /*fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
					//横軸
					baseline: currentDatetime,		//軸の描画位置  Improper: 'steppedArea'
					baselineColor: 'none',			//軸の色              Improper: 'steppedArea'
					viewWindowMode: 'explicit',		//{'pretty'|'maximized'|'explicit'}
					viewWindow: { min: minDatetime, max: maxDatetime },		//表示データ範囲
					direction: 1,					//軸の向き  default: 1
					//目盛
					textPosition: 'out',			//目盛値の表示位置
					textStyle: { /* color: 'black', */ /*fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
					slantedText: false,				//目盛値を斜体にして全体を表示
					maxTextLines: 2,				//目盛値の表示行の数
					maxAlternation: 1,				//目盛値の段違い表示の段数
					//mintextSpacing: /* <textStyle-font-size>, */
					//showTextEvery: 3,				//目盛値の表示間隔
					//format: 'M/dd k:mm',			//目盛フォーマット  eg, 23:59 + 1 hour =>> 24:59
					format: 'M/dd H:mm',			//目盛フォーマット  eg, 23:59 + 1 hour =>> 00:59
					//目盛線
					gridlines:      { color: '#b4b4b4', count: 10 },		//Available: 'bars', Improper: 'steppedArea', 1=24時間毎
					minorGridlines: { color: /*'#b4b4b4'*/'transparent', count: 1 },		//Available: 'bars', Improper: 'steppedArea'
				}
		};

		// グラフ描画
		var riverChart = new google.visualization.ComboChart(document.getElementById('riverChart_div'));
		riverChart.draw(data, options);
	 }
</c:if>
</script>
</html>
