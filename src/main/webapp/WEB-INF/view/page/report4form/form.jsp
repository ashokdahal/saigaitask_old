<%-- ４号様式の集計・総括の詳細タブからも使います。(/page/report4formGeneralization/detail-tab.jsp) --%>
<%@include file="../../common/lang_resource.jsp" %>
  <table width="900" border="0" >
  <tr>
    <td colspan="2"><strong><%=lang.__("No. 4 format (1st)")%></strong></td>
    <td width="430" rowspan="4"><table width="447" class="table02">
      <tr>
        <td width="201"><%=lang.__("Reporting date and time")%></td>
        <td width="217"><form:input path="reporttime"  size="20"/></td>
      </tr>
      <tr>
        <td><%=lang.__("Prefectures")%></td>
        <td><form:input path="pref"  size="20"/></td>
      </tr>
      <tr>
        <td><%=lang.__("Municipalities (fire department HQ name)")%></td>
        <td><form:input path="city"  size="20"/></td>
      </tr>
      <tr>
        <td><%=lang.__("Rapporteur name")%></td>
        <td><form:input path="reporter"  size="10"/></td>
      </tr>
    </table></td>
  </tr>
  <tr>
    <td colspan="2"><%=lang.__("(Disaster Overview immediate report)")%></td>
    </tr>
  <tr>
    <td width="194">&nbsp;</td>
    <td width="262"><u><%=lang.__("Fire Defense Agency receive name")%>：<form:input path="receiver" size="10" /></u></td>
    </tr>
  <tr>
    <td><u><%=lang.__("Disaster name")%>：<form:input path="disastername" size="10" /></u></td>
    <td><u><%=lang.__("(No.")%><form:input path="reportno" size="2" /><%=lang.__("Report)")%></u></td>
    </tr>
</table>

<table width="900" border="0">
  <tr>
    <td width="902"><table class="table01">
      <tr>
        <td width="37" rowspan="2"><%=lang.__("Disaster overview")%></td>
        <td colspan="2"><%=lang.__("Occurrence location")%></td>
        <td colspan="2"><form:input path="place"  size="20" cssStyle="width:90%;"/></td>
        <td width="127"><%=lang.__("Occurrence date and time")%></td>
        <td colspan="2"><form:input path="occurtime" size="25" cssStyle="width:90%;"/></td>
      </tr>
      <tr>
        <td height="134" colspan="7"><form:textarea rows="6" cols="70"  path="summary" cssStyle="width:90%;"/></td>
      </tr>
      <tr>
        <td rowspan="3"><%=lang.__("Damage situation")%></td>
        <td width="120" height="66" rowspan="2"><div align="center"><%=lang.__("Casualties")%></div></td>
        <td width="100"><div align="center"><%=lang.__("Deaths")%></div></td>
        <td width="98" height="66"><div align="right"><form:input path="casualties1" size="5" /><%=lang.__("People")%></div></td>
        <td width="109" height="66"><%=lang.__("Unknown")%><form:input path="casualties2" size="3" /><%=lang.__("People")%></td>
        <td height="66" rowspan="2"><div align="center"><%=lang.__("House")%></div></td>
        <td width="141" height="66"><%=lang.__("Fully destroyed")%> <form:input path="house1" size="3" />　<%=lang.__("Building<!--2-->")%></td>
        <td width="132"><%=lang.__("Partial damage")%><form:input path="house3" size="3" /><%=lang.__("Building<!--2-->")%></td>
        </tr>
      <tr>
        <td width="100"><div align="center"><%=lang.__("Injured")%></div></td>
        <td width="98" height="32"><div align="right"><form:input path="casualties3" size="5" /><%=lang.__("People")%></div></td>
        <td width="109" height="32"><%=lang.__("Total")%><form:input path="total" size="3" /> <%=lang.__("People")%></td>
        <td height="32"><%=lang.__("Partially destroyed")%><form:input path="house2" size="3" /><%=lang.__("Building<!--2-->")%></td>
        <td width="132"><%=lang.__("Inundation above floor")%><form:input path="house4" size="3" /><%=lang.__("Building<!--2-->")%></td>
      </tr>
      <tr>
        <td height="134" colspan="7"><form:textarea rows="6" cols="70"  path="status1" cssStyle="width:90%;"/></td>
      </tr>
      <tr>
        <td rowspan="2"><%=lang.__("Situation of emergency measures")%></td>
        <td height="66" colspan="2"><%=lang.__("Disaster response HQ ,etc.")%><br><%=lang.__("Installation condition")%></td>
        <td colspan="2"><%=lang.__("(Local prefectural gov.)")%><form:input path="headoffice1" size="20" /></td>
        <td height="66" colspan="3"><%=lang.__("(City)")%><form:input path="headoffice2" size="30" /></td>
      </tr>
      <tr>
        <td height="134" colspan="7"><form:textarea rows="6" cols="70"  path="status2" cssStyle="width:90%;"/></td>
      </tr>
      </table>
    <%=lang.__("(Attention) Submit initial report as far as you know and as soon as possible in thirty minutes after disaster happened. (It might be OK to type \"unconfirmed\" which is not confirmed officially.) ")%></td>
  </tr>
</table>
<table width="900" border="0">
  <tr>
    <td colspan="2">&nbsp;</td>
    <td width="448" rowspan="5">&nbsp;</td>
  </tr>
  <tr>
    <td colspan="2"><strong><%=lang.__("No. 4 format (2nd)")%></strong></td>
  </tr>
  <tr>
    <td colspan="2"><p><%=lang.__("(Damage situation immediate report)")%></p></td>
  </tr>
  <tr>

  </tr>
</table>
<table width="900" class="table01">
      <tr>
        <td colspan="3"><%=lang.__("City<!--2-->")%></td>
        <td colspan="2"></td>
        <td colspan="4"><%=lang.__("Type")%></td>
        <td width="188" colspan="2"><%=lang.__("Damage")%></td>
      </tr>
      <tr>
        <td colspan="3" rowspan="4"><%=lang.__("Disaster name, report number")%></td>
        <td height="134" colspan="2" rowspan="4">
        	<%=lang.__("Disaster name")%>　<br>
        	<br>
        	<%=lang.__("No.    report")%><br>
        	<br>
        	<%=lang.__("(As of     .    ,     at   :   )")%>
        </td>
        <td rowspan="23"><%=lang.__("Other")%></td>
        <td width="44" height="134" rowspan="2"><%=lang.__("Rice field")%></td>
        <td width="89"><%=lang.__("Outflow, burying")%></td>
        <td height="134">ha</td>
        <td height="134" colspan="2"><form:input path="field1" size="10" /></td>
      </tr>
      <tr>
        <td><%=lang.__("Flooding")%></td>
        <td height="35">ha</td>
        <td height="35" colspan="2"><form:input path="field2" size="10" /></td>
      </tr>
      <tr>
        <td height="66" rowspan="2"><%=lang.__("Farm")%></td>
        <td width="89"><%=lang.__("Outflow, burying")%></td>
        <td height="66">ha</td>
        <td height="66" colspan="2"><form:input path="farm1" size="10" /></td>
      </tr>
      <tr>
        <td><%=lang.__("Flooding")%></td>
        <td height="28">ha</td>
        <td height="28" colspan="2"><form:input path="farm2" size="10" /></td>
      </tr>
      <tr>
        <td colspan="3" rowspan="2"><%=lang.__("Rapporteur name")%></td>
        <td height="66" colspan="2" rowspan="2">&nbsp;</td>
        <td height="66" colspan="2"><%=lang.__("Educational facilities")%></td>
        <td width="29"><%=lang.__("Place")%></td>
        <td colspan="2"><form:input path="school" size="10" /></td>
        </tr>
      <tr>
        <td height="41" colspan="2"><%=lang.__("Hospital")%></td>
        <td width="29"><%=lang.__("Place")%></td>
        <td colspan="2"><form:input path="hospital" size="10" /></td>
      </tr>
      <tr>
        <td height="43" colspan="4"><%=lang.__("Type")%> </td>
        <td width="209" height="43"><%=lang.__("Damage")%></td>
        <td height="43" colspan="2"><%=lang.__("Road")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td height="43" colspan="2"><form:input path="road" size="10" /></td>
      </tr>
      <tr>
        <td width="43" rowspan="4"><%=lang.__("Human suffering")%></td>
        <td colspan="2"><%=lang.__("Deaths")%></td>
        <td height="43" width=43"><%=lang.__("People")%></td>
        <td height="43" ><form:input path="casualties21" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Bridge")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td height="43" colspan="2"><form:input path="bridge" size="10" /></td>
      </tr>
      <tr>
        <td colspan="2"><%=lang.__("Missing persons")%></td>
        <td height="43" width="43"><%=lang.__("People")%></td>
        <td height="43" ><form:input path="casualties22" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("River")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td height="43" colspan="2"><form:input path="river" size="10" /></td>
      </tr>
      <tr>
        <td width="44" height="43" rowspan="2"><%=lang.__("Injured")%></td>
        <td width="100"><%=lang.__("Seriously injured")%></td>
        <td height="43"><%=lang.__("People")%></td>
        <td height="43"><form:input path="casualties23" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Port")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td height="43" colspan="2"><form:input path="harbor" size="10" /></td>
      </tr>
      <tr>
        <td><%=lang.__("Slight injury")%></td>
        <td height="43"><%=lang.__("People")%></td>
        <td height="43" ><form:input path="casualties24" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Erosion control")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td height="43" colspan="2"><form:input path="landslide" size="10" /></td>
      </tr>
      <tr>
        <td height="43" rowspan="15"><%=lang.__("House damage")%></td>
        <td height="43" colspan="2" rowspan="3"><%=lang.__("Fully destroyed")%></td>
        <td height="43"><%=lang.__("Building<!--2-->")%></td>
        <td height="43"><form:input path="houseall1" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Cleaning facilities")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td height="43" colspan="2"><form:input path="gabage" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("Household")%></td>
        <td height="43"><form:input path="houseall2" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Cliff failure")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td height="43" colspan="2"><form:input path="cliff" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("People")%></td>
        <td height="43"><form:input path="houseall3" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Train service interruptions")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td height="43" colspan="2"><form:input path="railway" size="10" /></td>
  </tr>
      <tr>
        <td height="43" colspan="2" rowspan="3"><%=lang.__("Partially destroyed")%></td>
        <td height="43"><%=lang.__("Building<!--2-->")%></td>
        <td height="43"><form:input path="househalf1" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Damaged ship")%></td>
        <td height="43"><%=lang.__("Count<!--2-->")%></td>
        <td colspan="2"><form:input path="ship" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("Household")%></td>
        <td height="43"><form:input path="househalf2" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Water supply<!--2-->")%></td>
        <td height="43"><%=lang.__("Houses")%></td>
        <td colspan="2"><form:input path="water" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("People")%></td>
        <td height="43"><form:input path="househalf3" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Telephone")%></td>
        <td height="43"><%=lang.__("Circuit")%></td>
        <td colspan="2"><form:input path="telephone" size="10" /></td>
      </tr>
      <tr>
        <td height="43" colspan="2" rowspan="3"><%=lang.__("Partial damage")%></td>
        <td height="43"><%=lang.__("Building<!--2-->")%></td>
        <td height="43"><form:input path="housepart1" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Electricity")%></td>
        <td height="43"><%=lang.__("Houses")%></td>
        <td colspan="2"><form:input path="electricity" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("Household")%></td>
        <td height="43"><form:input path="housepart2" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Gas")%></td>
        <td height="43"><%=lang.__("Houses")%></td>
        <td colspan="2"><form:input path="gas" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("People")%></td>
        <td height="43"><form:input path="housepart3" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Block wall, etc.")%></td>
        <td height="43"><%=lang.__("Place")%></td>
        <td colspan="2"><form:input path="block" size="10" /></td>
      </tr>
      <tr>
        <td height="43" colspan="2" rowspan="3"><%=lang.__("Inundation above floor")%></td>
        <td height="43"><%=lang.__("Building<!--2-->")%></td>
        <td height="43"><form:input path="houseupper1" size="10" /></td>
        <td colspan="2" rowspan="3">&nbsp;</td>
        <td rowspan="3">&nbsp;</td>
        <td colspan="2" rowspan="3">&nbsp;</td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("Household")%></td>
        <td height="43"><form:input path="houseupper2" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("People")%></td>
        <td height="43"><form:input path="houseupper3" size="10" /></td>
      </tr>
      <tr>
        <td height="43" colspan="2" rowspan="3"><%=lang.__("Inundation under floor")%></td>
        <td height="43"><%=lang.__("Building<!--2-->")%></td>
        <td height="43"><form:input path="houselower1" size="10" /></td>
        <td height="43" colspan="3"><%=lang.__("Number of afflicted households")%></td>
        <td height="43"><%=lang.__("Household")%></td>
        <td height="43" colspan="2"><form:input path="suffer1" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("Household")%></td>
        <td height="43"><form:input path="houselower2" size="10" /></td>
        <td height="43" colspan="3"><%=lang.__("Number of victims")%></td>
        <td height="43"><%=lang.__("People")%></td>
        <td height="43" colspan="2"><form:input path="suffer2" size="10" /></td>
      </tr>
      <tr>
        <td height="43"><%=lang.__("People")%></td>
        <td height="43"><form:input path="houselower3" size="10" /></td>
        <td rowspan="3"><%=lang.__("Fire occurrence")%></td>
        <td height="43" colspan="2"><%=lang.__("Building")%></td>
        <td height="43"><%=lang.__("Items")%></td>
        <td colspan="2"><form:input path="fire1" size="10" /></td>
      </tr>
      <tr>
        <td rowspan="2"><%=lang.__("Ruinous house")%></td>
        <td height="43" colspan="2"><%=lang.__("Public facility")%></td>
        <td><%=lang.__("Building<!--2-->")%></td>
        <td height="43"><form:input path="build1" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Hazardous material")%></td>
        <td height="43"><%=lang.__("Items")%></td>
        <td colspan="2"><form:input path="fire2" size="10" /></td>
      </tr>
      <tr>
        <td height="43" colspan="2"><%=lang.__("Other")%></td>
        <td><%=lang.__("Building<!--2-->")%></td>
        <td height="43"><form:input path="build2" size="10" /></td>
        <td height="43" colspan="2"><%=lang.__("Other")%></td>
        <td height="43"><%=lang.__("Items")%></td>
        <td colspan="2"><form:input path="fire3" size="10" /></td>
      </tr>
      </table>
<br>
<table width="900" class="table01">
  <tr>
    <td height="43" colspan="3" nowrap><%=lang.__("Type")%></td>
    <td height="43" colspan="2" nowrap><%=lang.__("Damage")%></td>
    <td rowspan="4" nowrap><%=lang.__("Installation conditions of HQ")%><br><%=lang.__("Municipal disaster recovery")%></td>
    <td rowspan="2"><%=lang.__("Name")%></td>
    <td rowspan="2"><form:input path="headoffice21" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="2" nowrap><%=lang.__("Public educational facilities")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount1" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="2" nowrap><%=lang.__("Agriculture, forestry and fisheries facilities")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount2" size="10" /></td>
    <td ><%=lang.__("Installation")%></td>
    <td height="43"><form:input path="headoffice22" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="2" nowrap><%=lang.__("Public works facilities")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount3" size="10" /></td>
    <td ><%=lang.__("Dissolution")%></td>
    <td height="43"><form:input path="headoffice23" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="2" nowrap><%=lang.__("Other public facilities")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount4" size="10" /></td>
    <td height="43" rowspan="2" colspan="3"><%=lang.__("* For disaster response HQ, mayors of municipalities had been leader")%><br><%=lang.__("Fill out something only when you have established based on Disaster Countermeasures Basic Act.")%></td>
  </tr>
  <tr>
    <td height="43" colspan="2" nowrap><%=lang.__("Subtotal")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2">&nbsp;</td>
  </tr>
  <tr>
    <td rowspan="7" nowrap><%=lang.__("Other")%></td>
    <td height="43" colspan="1" nowrap><%=lang.__("Damaged agriculture")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount5" size="10" /></td>
    <td height="43" rowspan="6" colspan="3"><form:textarea path="status" rows="10" cols="35" cssStyle="width:90%;"/></td>
  </tr>
  <tr>
    <td height="43" colspan="1"><%=lang.__("Damaged forestry")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount6" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="1"><%=lang.__("Livestock damage")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount7" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="1"><%=lang.__("Damaged fisheries")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount8" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="1"><%=lang.__("Commerce and industry damage")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount9" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="1">&nbsp;</td>
    <td height="43">&nbsp;</td>
    <td height="43" colspan="2">&nbsp;</td>
  </tr>
  <tr>
    <td height="43" colspan="1"><%=lang.__("Other")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2"><form:input path="amount10" size="10" /></td>
    <td colspan="1"><%=lang.__("Accumulated number of firefighter going into action")%></td>
    <td><%=lang.__("People")%></td>
    <td><form:input path="fireman1" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="2"><%=lang.__("Total amount of damage")%></td>
    <td height="43"><%=lang.__("1000 yen")%></td>
    <td height="43" colspan="2">&nbsp;</td>
    <td colspan="1"><%=lang.__("Accumulated number of volunteer fire corps going into action")%></td>
    <td><%=lang.__("People")%></td>
    <td><form:input path="fireman2" size="10" /></td>
  </tr>
  <tr>
    <td height="43" colspan="2"><%=lang.__("Notes")%></td>
    <td height="200" colspan="6">
    <form:textarea path="note2" rows="10" cols="60" cssStyle="width:90%;"/>
    </p></td>
  </tr>
</table>
<%=lang.__("*1 Cost of damage can be omitted.")%><br>
<%=lang.__("*2 Report the number of emergency call 119 by increments of 10, such as 30, 50 (more than 50, type many)")%>


