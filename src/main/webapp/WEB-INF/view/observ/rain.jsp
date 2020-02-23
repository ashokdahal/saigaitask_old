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
<body>

<c:set var="rain" value="${observatoryrainInfo}"/>
<div class="container" style="width:900px; /*height:600px; */margin:0 auto;">
	<div class="header" style="width:900px; height:45px; background-color:#000000; color:#BEF1FF; display:block; line-height:45px;">
		<span style="font-weight:bold; margin:4px;"><%=lang.__("Telemeter")%>&nbsp;<%=lang.__("Rainfall")%>&nbsp;&nbsp;${f:h(rain.name)}（${f:h(rain.readname)}）</span>
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
					<a href="${f:url('/observ/rain')}/${id}/${Observ.HOUR}?time=${time}"><%=lang.__("On the hour every hour")%></a>&nbsp;/&nbsp;<span style="font-weight:bold">${Observ.MIN}<%=lang.__(" minutes each")%></span>
	</c:when>
	<c:otherwise>
					<span style="font-weight:bold;"><%=lang.__("On the hour every hour")%></span>&nbsp;/&nbsp;<a href="${f:url('/observ/rain')}/${id}/${Observ.MIN}?time=${time}">${Observ.MIN}<%=lang.__(" minutes each")%></a>
	</c:otherwise>
</c:choose>
				</td>
			</tr>
		</table>
		<table width="900" border="0">
			<tr>
				<td colspan="2">
					<!-- 観測地点情報 -->
					<table border="1" style="font-size:80%; text-align:center;">
						<caption></caption>
						<tr>
							<th width="80"class="head"><%=lang.__("The name of the water system")%></th>
							<th width="120" class="head"><%=lang.__("River name")%></th>
							<th width="100" class="head"><%=lang.__("Observation name")%></th>
							<!-- <td width="60" class="head">管理区分</th> -->
							<th width="140" class="head"><%=lang.__("Jurisdiction")%></th>
							<th width="220" class="head"><%=lang.__("Location")%></th>
							<th width="80" class="head"><%=lang.__("Altitude")%></th>
							<th width="160" class="head"><%=lang.__("Coordinates")%></th>
						</tr>
						<tr>
							<td rowspan="2">${f:h(rain.basin) }</td>
							<td rowspan="2">${f:h(rain.river) }</td>
							<td rowspan="2">${f:h(rain.name) }</td>
							<!-- <td rowspan="2">{国河川|自治体|etc.}</td> -->
							<td rowspan="2">${f:h(rain.officename) }</td>
							<td rowspan="2">${f:h(rain.address) }</td>
							<td rowspan="2">${f:h(rain.altitude) }&nbsp;m</td>
							<td><%=lang.__("Latitude")%>&nbsp;<fmt:formatNumber value="${f:h(lat[0])}" pattern="000" />°<fmt:formatNumber value="${f:h(lat[1])}" pattern="00" />′<fmt:formatNumber value="${f:h(lat[2])}" pattern="00.00" />″</td>
						</tr>
						<tr>
							<td><%=lang.__("Longitude")%>&nbsp;<fmt:formatNumber value="${f:h(lon[0])}" pattern="000" />°<fmt:formatNumber value="${f:h(lon[1])}" pattern="00" />′<fmt:formatNumber value="${f:h(lon[2])}" pattern="00.00" />″</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td width="400" style="vertical-align:top;">
					<br>
					<table border="1" style="font-size:80%" class="list">
						<thead>
							<caption></caption>
							<tr>
								<th colspan="2" class="head" width="80"><%=lang.__("Times of day")%></th>
								<th colspan="2" class="head" width="100"><%=lang.__("Rainfall (mm)")%></th>
								<th class="head" width="100"><%=lang.__("Cumulative rainfall (mm)")%></th>
							</tr>
						</thead>
						<tbody>
<c:set var="pre_date" value=""/>
<c:forEach var="e" varStatus="s" items="${tableItems}">
	<c:set var="dateParts" value="${fn:split(e.datelabel, '/')}" />
	<c:set var="md" value="${dateParts[1]}/${dateParts[2]}" />
							<tr>
								<td style="border-style:none none none solid; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'};">${pre_date!=e.datelabel?md:'&nbsp;'}</td>
								<td style="border-style:none solid none none; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'};">${f:h(e.timelabel)}</td>
								<td style="border-style:none none none solid; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'};" width="70px">${(empty e.contentscode1)?'N/A':(e.contentscode1!=0)?e.contentsname1:e.itemdata1}</td>
								<td style="border-style:none solid none none; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'}; text-align:center;">${(e.itemdataarrow1) }</td>
								<td style="border-style:none solid; border-top:${pre_date!=e.datelabel?'solid 1px black':'none'};">${(empty e.contentscode2)?'N/A':(e.contentscode2!=0)?e.contentsname2:e.itemdata2}</td>
							</tr>
	<c:set var="pre_date" value="${e.datelabel}"/>
</c:forEach>
						</tbody>
					</table>
				</td>
				<td width="500">
					<div id="rainChart_div" style="width:450px; height:450px"></div>&nbsp;
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
							<li><a class="btn blue" href="#" onClick="${rain.iframe?'self.parent.tb_remove();':'window.close();'} return false;"><%=lang.__("Close")%></a></li>
							<li><a class="btn blue" href="javascript:print();"><%=lang.__("Print")%></a></li>
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
		data.addColumn({type:'number',   role:'data',   label:'<%=lang.__("Rainfall (mm)")%>'});
		data.addColumn({type:'number',   role:'data',   label:'<%=lang.__("Cumulative rainfall (mm)")%>'});

		//レコードの追加([datetime, num, num])<%-- 累加雨量が0の場合は、グラフを描画しないようにするため値をnullにする. --%>
		data.addRows([
<c:forEach var="e" varStatus="s" items="${graphItems}">
					[{v:new Date('${e.datevalue } ${e.timevalue }'), f:'${e.datelabel } ${e.timelabel }'}, {v:${(e.contentscode1==0)?e.itemdata1:'null' }, f:'${(e.contentscode1==0)?e.itemdata1:e.contentsname1 }'}, {v:${(e.contentscode2==0 && e.itemdata2!='0.0')?e.itemdata2:'null' }, f:'${(e.contentscode2==0)?e.itemdata2:e.contentsname2 }'}]${s.last?'':',' }
</c:forEach>
		]);

//		//フォーマッターの作成
//		var formatter_dec1 = new google.visualization.NumberFormat({fractionDigits: 1});			//小数点一位
//		var formatter_ymd6HHmm = new google.visualization.DateFormat({pattern: "yyyy/MM/dd HH:mm"});	//日付を24時間表記  eg, 23:59 + 1 hour =>> 00:59
//		var formatter_ymd6kkmm = new google.visualization.DateFormat({pattern: "yyyy/MM/dd kk:mm"});	//日付を24時間表記  eg, 23:59 + 1 hour =>> 24:59
//
//		//データの整形
//		formatter_ymd6HHmm.format(data, 0);	//日付をフォーマット		<%-- 下のフォーマットでは24:00を正しく表記できないため、こちらにしています. --%>
//		//formatter_ymd6kkmm.format(data, 0);	//日付をフォーマット
//		formatter_dec1.format(data, 1);		//フォーマットをdataのフィールド１に適用
//		formatter_dec1.format(data, 2);		//フォーマットをdataのフィールド２に適用

		//グラフのプロット期間
//		var currentDatetime = new Date('${graphItems[fn:length(graphItems)-1].datevalue } ${graphItems[fn:length(graphItems)-1].timevalue }');
		var minDatetime = new Date('${graphItems[0].datevalue } ${graphItems[0].timevalue }');
		var maxDatetime = new Date('${graphItems[fn:length(graphItems)-1].datevalue } ${graphItems[fn:length(graphItems)-1].timevalue }');


		//グラフのY軸
		var minValueY1 = 'automatic';		//グラフ要素の最小値
		var minValueY2 = 'automatic';		//グラフ要素の最小値
		var maxValueY1 = 'automatic';		//グラフ要素の最大値
		var maxValueY2 = 'automatic';		//グラフ要素の最大値
//		var gridlineCnt = 'automatic';	//補助線の数
		maxValueY1 = (data.getColumnRange(1)['max']>100)?200:(data.getColumnRange(1)['max']>50)?100:(data.getColumnRange(1)['max']>20)?50:20;
		maxValueY2 = (data.getColumnRange(2)['max']>100)?200:(data.getColumnRange(2)['max']>50)?100:(data.getColumnRange(2)['max']>20)?50:20;

		//グラフ仕様
		//  X軸  （時刻）	：datetime型（目盛線を用いるため continuous axis になる datetime型を採用）
		//  Y軸１（雨量）	：barsチャート
		//  Y軸２（累加雨量）：lineチャート
		//グラフオプションの設定
		var options = {
				//基本設定
				width: 450, height: 450,			//グラフサイズ
				backgroundColor: { fill: 'white', stroke: 'black', strokeWidth: 0 },	//グラフエリア（背景色、枠線色、枠線幅）
				//chartArea: { left: 'auto', top: 'auto', width: '61.8%', height: '61.8%' },		//プロットエリア
				fontSize: 11,
				fontName: 'Arial',				//default: 'Arial'
				enableInteractivity: true,		//イベント動作
				bar: { groupWidth: '100%' },		//バーとバーの間隔, default: 61.8%, Available: 'bars'

				//ツールチップ
				focusTarget: 'category',			//ツールチップ内容 {'datum'|'category'}
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
					position: 'top',		//表示位置
					textStyle: { /* color: 'black', */ /* fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
					alignment: 'center'		//文字寄せ位置
				},

				//複合グラフタイプの指定
				series: {
					0: { //雨量
						type: 'bars',
						color: '#00ffff',		//グラフの色
						targetAxisIndex: 0,		//vAxis設定の指定
						//areaOpacity: 1.0,		//透過性（0.0-1.0）, default: 0.3, Available: 'steppedArea'
						visibleInLegend: true
					},
					1: { //累加雨量
						type: 'line',
						color: '#000000',		//グラフの色
						targetAxisIndex: 1,		//vAxis設定の指定
						PointSize: 0,			//グラフの点,   Available: 'line',
						lineWidth: 2,			//グラフの線幅, Available: 'line',
						visibleInLegend: true
					}
				},
				//縦軸
				axisTitlesPosition: 'out',		//縦軸のタイトルの位置
				vAxes: {
					0: { //左軸用(bars)
						title: '<%=lang.__("Rainfall (mm)")%>',
						//データ範囲
						viewWindowMode: 'pretty',	//{'pretty'|'maximized'|'explicit'}
						//目盛
						format: '0',
						//目盛線
						gridlines:      { color: '#b4b4b4', count: 6 },	//目盛線
						minorGridlines: { color: '#f0f0f0', count: 1 },	//補助目盛線
						minValue: 0,
						maxValue: maxValueY1
					},
					1: { //右軸用(line)
						title: '<%=lang.__("Cumulative rainfall (mm)")%>',
						//データ範囲
						viewWindowMode: 'pretty',	//{'pretty'|'maximized'|'explicit'}
						//目盛
						format: '0',
						//目盛線
						gridlines:      { color: '#b4b4b4', count: 6 },	//目盛線
						minorGridlines: { color: '#f0f0f0', count: 1 },	//補助目盛線
						minValue: 0,
						maxValue: maxValueY2
					}
				},
				//横軸
				hAxis: {
					//横軸タイトル
					//title: '時刻',
					titleTextStyle: { /* color: 'black', */ /*fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
					//横軸
					baseline: minDatetime,			//軸の描画位置  Improper: steppedArea
					Color: 'none',					//軸の色              Improper: steppedArea
					viewWindowMode: 'explicit',		//pretty or explicit
					viewWindow: { min: minDatetime, max: maxDatetime },		//表示データ範囲
					direction: 1,					//軸の向き  default: 1
					//目盛
					textPosition: 'out',			//目盛値の表示位置
					textStyle: { /* color: 'black', */ /*fontName: <global-font-name>, */ /* fontSize: <global-font-size> */fontSize: 14 },
					slantedText: false,				//目盛値を斜体にして全体を表示
					maxTextLines: 2,				//目盛値の表示行の数
					maxAlternation: 1,				//目盛値の段違い表示の段数
					//mintextSpacing: /* <textStyle-font-size>, */
					//showTextEvery: 3,				//目盛値の表示間隔
					//format: 'M/dd k:mm',			//目盛フォーマット  eg, 23:59 + 1 hour =>> 24:59
					format: 'M/dd H:mm',			//目盛フォーマット  eg, 23:59 + 1 hour =>> 00:59
					//目盛線
					gridlines:      { color: '#b4b4b4', count: 5 },		//Available: 'bars', Improper: 'steppedArea', 1=24時間毎
					minorGridlines: { color: '#b4b4b4', count: 1 },		//Available: 'bars', Improper: 'steppedArea'
				}
		};
<%-- グラフデバッグ用。
		//グラフ仕様
		//  X軸  （時刻）	：string型
		//  Y軸１（雨量）	：steppedAreaチャート
		//  Y軸２（累加雨量）：lineチャート
		//グラフオプションの設定
		var options2 = {
				//基本設定
				width: 500, height: 450,		//グラフサイズ
				backgroundColor: { fill: 'white', stroke: 'black', strokeWidth: 0 },			//グラフエリア（背景色、枠線色、枠線幅）
				//chartArea: { left: 'auto', top: 'auto', width: 'auto', height: 'auto' },		//プロットエリア
				fontSize: 11,
				fontName: 'Arial',	//default: 'Arial'
				enableInteractivity: true,		//ホバーテキスト表示
				//bar: { groupWidth: '100%' },	//バーとバーの間隔, default: 61.8%, Available: 'bars'

				//ツールチップ
				focusTarget: 'category',			//ツールチップ内容 {'datum'|'category'}
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
					position: 'none',		//表示位置
					textStyle: { /* color: 'black', */ /* fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
					alignment: 'center'	//文字寄せ位置
				},

				//複合グラフタイプの指定
				series: {
					0: {
						type: 'steppedArea',
						color: '#84ffff',		//グラフの色
						targetAxisIndex: 0,		//vAxis設定の指定
						areaOpacity: 1.0,		//透過性（0.0-1.0）, default: 0.3, Available: 'steppedArea'
						visibleInLegend: true
					},
					1: {
						type: 'line',
						color: 'black',			//グラフの色
						targetAxisIndex: 1,		//vAxis設定の指定
						PointSize: 0,			//グラフの点,   Available: 'line',
						lineWidth: 2,			//グラフの線幅, Available: 'line',
						visibleInLegend: true
					}
				},
				//縦軸
				axisTitlesPosition: 'out',		//縦軸のタイトルの位置
				vAxes: {
					//左軸用(steppedArea)
					0: {
						title: '雨量(mm)',
						//データ範囲
						viewWindowMode: 'pretty',		//pretty or explicit
						//目盛線
						gridlines: { color: '#d2d2d2', count: 6 },		//目盛線
						minorGridlines: { color: '#e6e6e6', count: 1 },		//補助目盛線
						minValue: 0,
						maxValue: maxValueY1
					},
					//右軸用(line)
					1: {
						title: '累加雨量(mm)',
						//データ範囲
						viewWindowMode: 'pretty',		//pretty or explicit
						//目盛線
						gridlines:      { color: '#d2d2d2', count: 6 },		//目盛線
						minorGridlines: { color: '#e6e6e6', count: 1 },		//補助目盛線
						minValue: 0,
						maxValue: maxValueY2
					}
				},
				//横軸
				hAxis: {
					//横軸タイトル
					title: '時刻',
					titleTextStyle: { /* color: 'black', */ /*fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
					//横軸
					//baseline: 0,					//軸の描画位置			//Improper: steppedArea
					//baselineColor: 'none',			//軸の色				//Improper: steppedArea
					viewWindowMode: 'pretty',		//pretty or explicit
					//viewWindow: { min: 'auto', max: 'auto' },		//表示領域
					direction: 1,					//軸の向き  default: 1
					//目盛
					textPosition: 'out',			//目盛値の表示位置
					textStyle: { /* color: 'black', */ /*fontName: <global-font-name>, */ /* fontSize: <global-font-size> */ },
					slantedText: false,				//目盛値を斜体にして全体を表示
					TextLines: 2,					//目盛値の表示行の数
					maxAlternation: 1,				//目盛値の段違い表示の段数
					showTextEvery: 6,				//目盛値の表示間隔
					//format: 'kk:mm',				//目盛フォーマット  eg, 23:59 + 1 hour =>> 24:59
					//目盛線
					//gridlines:      { color: '#d2d2d2', count: 5 },			//Available: bars, Improper: steppedArea
					//minorGridlines: { color: '#e6e6e6', count: 1 },		//Available: bars, Improper: steppedArea
				},
		};
--%>
		// グラフ描画
		var rainChart = new google.visualization.ComboChart(document.getElementById('rainChart_div'));
		rainChart.draw(data, options);
	 }
</c:if>
</script>
</html>
