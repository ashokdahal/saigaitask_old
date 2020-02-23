/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;
import javax.transaction.Status;
import javax.validation.Valid;

import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult.FileResult;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Menutype;
import jp.ecom_plat.saigaitask.dto.ListEditDto;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.PostingphotolayerData;
import jp.ecom_plat.saigaitask.entity.db.PostingphotolayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablerowstyleInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.page.ListForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.service.db.PostingphotolayerDataService;
import jp.ecom_plat.saigaitask.service.db.PostingphotolayerInfoService;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

/**
 * 写真リストページを表示するアクションクラスです.
 * spring checked take 5/14
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/page/postingphoto")
public class PostingphotoAction extends ListAction {

	/** ページ種別 */
	public static final String PAGE_TYPE = "postingphoto";

	@Resource
	protected PostingphotolayerInfoService postingphotolayerInfoService;
	@Resource
	protected PostingphotolayerDataService postingphotolayerDataService;
	
	public PostingphotolayerInfo photolayerInfo;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("photolayerInfo", photolayerInfo);
	}
	
	/**
	* 写真リストページを表示する.
	 * @return フォワード先 リストページ
	 */
	//@org.springframework.web.bind.annotation.RequestMapping(value={"","/index"})//Overrideなので不要
	@Override
	public String index(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm, BindingResult bindingResult) {
		this.listForm = listForm;
		initPage(PAGE_TYPE, listForm);
		pageDto.setEnableFullscreen(true);

		long menuid = pageDto.getMenuInfo().id;
		long taskid = pageDto.getMenutaskInfo().id;
		try {
			content(taskid, menuid);

			List<PostingphotolayerInfo> photolayerInfoList = postingphotolayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
			if (photolayerInfoList.size() > 0)
				photolayerInfo = photolayerInfoList.get(0);
			else
				throw new ServiceException(lang.__("No configuration of posted photo layer"));

			long phototableid = photolayerInfo.tablemasterinfoid;
			TablemasterInfo tableInfo = tablemasterInfoService.findById(phototableid);

			// 投稿写真レイヤがコピーしない想定なので、コピーしている場合は警告表示
			if(!table.equals(tableInfo.layerid)) {
				ActionMessages errors = new ActionMessages();
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Copy flag of posted photo layer must be false."), false));
				ActionMessagesUtil.addErrors(bindingResult, errors);
			}

			//項目の追加
			addPhotoColumn();

		} catch(ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to display list."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);
		
		return "page/postingphoto/index";
	}

	protected void addPhotoColumn() {
		//基本的に編集なし
		if(colinfoItems==null) throw new ServiceException(lang.__("Set menu table info of posted photo layer"));
		if(colinfoItems.size()==0) throw new ServiceException(lang.__("Set table list item info of posted photo layer"));
		for (TablelistcolumnInfo colinfo : colinfoItems) {
			colinfo.editable = false;
			colinfo.highlight = false;
		}
		TablelistcolumnInfo lastcol = colinfoItems.get(colinfoItems.size()-1);
		List<TrackData> tracklist = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), false);
		tracklist.addAll(trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true));
		String[] editClass2 = null;
		editClass2 = Arrays.copyOf(editClass, editClass.length+1+tracklist.size()+1);
		editClass = editClass2;

		int colsize = colinfoItems.size();
		int idx = 0;
		//写真項目の追加
		TablelistcolumnInfo photocol = new TablelistcolumnInfo();
		photocol.attrid = "_feature_files";
		photocol.editable = false;
		photocol.highlight = false;
		photocol.disporder = lastcol.disporder +idx+1;
		photocol.name = lang.__("Picture");
		photocol.sortable = false;
		editClass[colsize+idx] = "Image";
		
		for (BeanMap map : result) {
			Vector<FileResult> filelist;
			try {
				// レイヤコピーフラグがtrueなら、tracktable_infoの方のレイヤから添付ファイルリストを取得する。
				// （投稿先はコピー元のはず）
				filelist = FeatureDB.getFeatureFileList(table, (long)map.get("gid"));
				String fileurl = null;
				if (filelist.size() > 0) {
					FileResult fileres = filelist.get(0);
					fileurl = fileres.url;
				}
				map.put("_feature_files", fileurl);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		colinfoItems.add(photocol);
		
		//災害項目の追加
		List<PostingphotolayerData> datalist = postingphotolayerDataService.findByPostingphotolayerInfoId(photolayerInfo.id);
		for (TrackData track : tracklist) {
			idx++;
			TablelistcolumnInfo trackcol = new TablelistcolumnInfo();
			trackcol.id = -1l;
			trackcol.attrid = "track"+track.id;
			trackcol.editable = true;
			trackcol.highlight = false;
			trackcol.disporder = lastcol.disporder +idx+1;
			trackcol.name = track.name;
			trackcol.sortable = false;
			editClass[colsize+idx] = "Checkbox";
			
			for (BeanMap map : result) {
				long photogid = (long)map.get("gid");
				boolean copy = false;
				for (PostingphotolayerData data : datalist) {
					if (data.photogid.equals(photogid) && data.copytrackdataid.equals(track.id)) {
						copy = true;
						break;
					}
				}
				map.put("track"+track.id, copy);
			}
			colinfoItems.add(trackcol);
			checkStr.put("track"+track.id, "true");
		}
		//平常時
		idx++;
		TablelistcolumnInfo trackcol = new TablelistcolumnInfo();
		trackcol.id = -1l;
		trackcol.attrid = "track0";
		trackcol.editable = true;
		trackcol.highlight = false;
		trackcol.disporder = lastcol.disporder +idx+1;
		trackcol.name = lang.__("Peace time");
		trackcol.sortable = false;
		editClass[colsize+idx] = "Checkbox";
		for (BeanMap map : result) {
			long photogid = (long)map.get("gid");
			boolean copy = false;
			for (PostingphotolayerData data : datalist) {
				if (data.photogid.equals(photogid) && data.copytrackdataid.equals(0l)) {
					copy = true;
					break;
				}
			}
			map.put("track0", copy);
		}
		colinfoItems.add(trackcol);
		checkStr.put("track0", "true");
	}
	
	/**
	 * 更新処理
	 * @return null
	 */
	@Transactional(propagation=Propagation.NEVER)
	//@org.springframework.web.bind.annotation.RequestMapping("/update")//Overrideなので不要
	@Override
	@ResponseBody
	public String update(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm) {
		MapDB mapDB = MapDB.getMapDB();
		List<PostingphotolayerInfo> photolayerInfoList = postingphotolayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if (photolayerInfoList.size() > 0)
			photolayerInfo = photolayerInfoList.get(0);
		else
			throw new ServiceException(lang.__("No configuration of posted photo layer"));
		TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
		TrackmapInfo mapInfo = trackmapInfoService.findByTrackDataId(trackData.id);
		String serverRootUrl = fileUploadService.getEcommapServerRootUrl();
		MapmasterInfo mapmaster = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		UserInfo userInfo = mapDB.getAuthIdUserInfo(loginDataDto.getEcomUser());
		
		long fid = 0;
		try {
			//コミットされる前にページの読み込みが走る場合があるので。トランザクションを別管理にする。
			userTransaction.begin();
		
    	//テーブルごとに分ける
    	Map<String, List<ListEditDto>> datamap = new HashMap<String, List<ListEditDto>>();
    	Map<String, List<ListEditDto>> delmap = new HashMap<String, List<ListEditDto>>();
		for (ListEditDto data : listForm.saveDatas) {
			String ids[] = data.id.split(":");
			//String table = ids[0];
			//String attrid = ids[1];
			//String key = ids[2];
			//String id = ids[3];
			logger.debug("datas : "+ids[0]+","+ids[1]+","+ids[2]+","+ids[3]+","+data.value);
			
			List<ListEditDto> list = null;
			if (data.value.equals("true")) {
				list = datamap.get(ids[1]);
				if (list == null) {
					list = new ArrayList<ListEditDto>();
					datamap.put(ids[1], list);
				}
			}
			else {
				list = delmap.get(ids[1]);
				if (list == null) {
					list = new ArrayList<ListEditDto>();
					delmap.put(ids[1], list);
				}
			}
			list.add(data);
		}
		
		//登録、削除
		List<TrackData> tracklist = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), false);
		tracklist.addAll(trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true));
		TrackData heiji = new TrackData();//平常時
		heiji.id = 0l;
		tracklist.add(heiji);
		for (TrackData track : tracklist) {
			TracktableInfo tracktableInfo = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(photolayerInfo.copytablemasterinfoid, track.id);
			long mapid = 0;
			String copylayerId = null;
			if (track.id == 0l) {//平常時
				mapid = mapmaster.mapid;
				TablemasterInfo tminfo = tablemasterInfoService.findById(photolayerInfo.copytablemasterinfoid);
				if (tminfo != null)
					copylayerId = tminfo.layerid;
			}
			else {
				TrackmapInfo tmap = trackmapInfoService.findById(tracktableInfo.trackmapinfoid);
				mapid = tmap.mapid;
				if (tracktableInfo != null)
					copylayerId = tracktableInfo.layerid;
			}
			LayerInfo layerInfo = mapDB.getLayerInfo(copylayerId);
			List<ListEditDto> list = datamap.get("track"+track.id);
			//新規
			if (list != null) {
				for (ListEditDto data : list) {
					String ids[] = data.id.split(":");
					String layerId = ids[0];
					long gid = Long.parseLong(ids[3]);
					PostingphotolayerData photo = postingphotolayerDataService.findByCopytrackDataIdAndPhotoGId(track.id, gid);
					if (photo == null) {
						// 被災写真投稿レイヤの写真振り分け画面は時系列対応の必要なしだが、時間パラメータを渡すようにしておく
						Date[] timeParam = listForm.timeParams();
						JSONObject jobj = mapService.getContents(loginDataDto.getEcomUser(), mapInfo.mapid, layerId, gid, timeParam);
						
						HashMap<String, String> attributes = new HashMap<String, String>();

						if (StringUtil.isNotEmpty(photolayerInfo.copycommentAttrid))
							attributes.put(photolayerInfo.copycommentAttrid, getValue(jobj, photolayerInfo.commentAttrid));
						if (StringUtil.isNotEmpty(photolayerInfo.copygroupAttrid))
							attributes.put(photolayerInfo.copygroupAttrid, getValue(jobj, photolayerInfo.groupAttrid));
						if (StringUtil.isNotEmpty(photolayerInfo.copynameAttrid))
							attributes.put(photolayerInfo.copynameAttrid, getValue(jobj, photolayerInfo.nameAttrid));
						if (StringUtil.isNotEmpty(photolayerInfo.copycontactAttrid))
							attributes.put(photolayerInfo.copycontactAttrid, getValue(jobj, photolayerInfo.contactAttrid));
						if (StringUtil.isNotEmpty(photolayerInfo.copytimeAttrid))
							attributes.put(photolayerInfo.copytimeAttrid, getValue(jobj, photolayerInfo.timeAttrid));
						if (StringUtil.isNotEmpty(photolayerInfo.copydirectionAttrid) && StringUtil.isNotEmpty(photolayerInfo.directionAttrid))
							attributes.put(photolayerInfo.copydirectionAttrid, getValue(jobj, photolayerInfo.directionAttrid));
						if (StringUtil.isNotEmpty(photolayerInfo.copyheightAttrid) && StringUtil.isNotEmpty(photolayerInfo.heightAttrid))
							attributes.put(photolayerInfo.copyheightAttrid, getValue(jobj, photolayerInfo.heightAttrid));
						
						JSONArray jgeom = (JSONArray)jobj.get("geom");
						String wkt = jgeom.get(0).toString();
						if (layerInfo.getGeometryType().equals("POLYGON")) {
							Geometry pt = getGeometryFromWKT(wkt);
							Polygon pgn = pointToPolygon(pt);
							wkt = pgn.toString();
						}
						else if (layerInfo.getGeometryType().equals("MULTIPOLYGON")) {
							Geometry pt = getGeometryFromWKT(wkt);
							Polygon pgn = pointToPolygon(pt);
							Polygon polys[] = new Polygon[1];
							polys[0] = (Polygon)pgn;
							GeometryFactory gf = new GeometryFactory();
							Geometry mpoly = gf.createMultiPolygon(polys);
							wkt = mpoly.toString();
						}
						
						fid = mapService.insertFeature(userInfo.authId, copylayerId, wkt, attributes);
						
						if (fid > 0l) {
							//写真アップロード
							Vector<FileResult> filelist = FeatureDB.getFeatureFileList(layerId, gid);
							if (filelist.size() > 0) {
								FileResult fres = filelist.get(0);
								mapDB.insertFeatureFile(copylayerId, fid, mapid, userInfo.userId, fres.getUrl(), fres.getTitle(), serverRootUrl);
							}
							
							//写真コピー情報
							PostingphotolayerData postData = new PostingphotolayerData();
							postData.postingphotolayerinfoid = photolayerInfo.id;
							postData.layerid = layerId;
							postData.photogid = gid;
							postData.copygid = fid;
							postData.copylayerid = copylayerId;
							postData.copytime = new Timestamp(System.currentTimeMillis());
							postData.copytrackdataid = track.id;
							
							postingphotolayerDataService.insert(postData);
						}
					}
				}
			}
			//削除
			list = delmap.get("track"+track.id);
			if (list != null) {
				for (ListEditDto data : list) {
					String ids[] = data.id.split(":");
					long gid = Long.parseLong(ids[3]);
					PostingphotolayerData photo = postingphotolayerDataService.findByCopytrackDataIdAndPhotoGId(track.id, gid);
					if (photo != null) {
						FeatureDB.deleteFeature(userInfo, photo.copylayerid, photo.copygid, new Timestamp(System.currentTimeMillis()));
						
						postingphotolayerDataService.delete(photo);
					}
				}
			}
		}
		
		//try {
			//HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
			//httpServletResponse.setContentType("application/json");
			//httpServletResponse.setCharacterEncoding("UTF-8");
			//PrintWriter sendPoint = new PrintWriter(httpServletResponse.getOutputStream());

			//Entity→JSON形式に変換して出力します。
			//sendPoint.println("[{\"id\":"+fid+", \"nullAlert\":\""+nullAlert+"\"}]");
			//sendPoint.println("[{\"id\":"+fid+"}]");
			//sendPoint.flush();
			//sendPoint.close();
			return "[{\"id\":"+fid+"}]";

		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		//	throw new ServiceException(e);
		//}
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(loginDataDto.logInfo(), e);
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e1) {
				logger.error(loginDataDto.logInfo(), e1);
			}
		} finally {
			try {
				if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
					userTransaction.commit();
				} else {
					userTransaction.rollback();
				}
			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
			}
		}
		
		return null;
	}
	
	protected String getValue(JSONObject jobj, String attrid) throws JSONException
	{
		JSONArray ary = jobj.getJSONArray("attrs");
		for (int i = 0; i < ary.length(); i++) {
			JSONObject attr = ary.getJSONObject(i);
			String key = attr.getString("attrId");
			if (key.equals(attrid)) {
				return attr.getString("attrValue");
			}
		}
		return null;
	}

	public Polygon pointToPolygon(Geometry point)
	{
		double d = 3600000d;
		GeometryFactory gf = new GeometryFactory();
		Coordinate[] coords = new Coordinate[4];
		double x = point.getCoordinate().x*d;
		double y = point.getCoordinate().y*d;
		coords[0] = new Coordinate(x/d, y/d);
		coords[1] = new Coordinate((x+1)/d, y/d);
		coords[2] = new Coordinate(x/d, (y+1)/d);
		coords[3] = new Coordinate(x/d, y/d);
		Polygon pgn = gf.createPolygon(coords);
		return pgn;
	}
	
	public static Geometry getGeometryFromWKT(String wkt) throws com.vividsolutions.jts.io.ParseException
	{
		if (wkt == null) return null;
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
		WKTReader reader = new WKTReader( geometryFactory );
		return (Geometry) reader.read(wkt);
	}

	/**
	 * ページ表示
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/tablebody/{menutaskid}/{menuid}")
	@ResponseBody
	public String tablebody(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm){

		long menuid = listForm.menuid;
		long menutaskid = listForm.menutaskid;
		pageDto.setMenuInfo(menuInfoService.findById(menuid));
		if ((loginDataDto.getTrackdataid() == 0 && !loginDataDto.isMaster() && !loginDataDto.isUsual()) || !loginDataDto.isEdiable())
			pageDto.setViewMode(true);

		content(model, listForm);

		List<PostingphotolayerInfo> photolayerInfoList = postingphotolayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if (photolayerInfoList.size() > 0)
			photolayerInfo = photolayerInfoList.get(0);
		else
			throw new ServiceException(lang.__("No configuration of posted photo layer"));

		addPhotoColumn();
		
		if (key == null) return null;

		boolean hasmap = !pageDto.getMenuInfo().menutypeid.equals(Menutype.LIST);
		//boolean btns = ((key.equals("gid") || key.equals("_orgid")) && !pageDto.getMenuInfo().menutypeid.equals(Menutype.LIST)) || detailable || historiable;
		StringBuffer buff = new StringBuffer();
		if (result == null) return null;

		//<c:forEach var="e" varStatus="s" items="${result}">
		for (BeanMap map : result) {

			if (filterIds.get(map.get(key).toString()) != null && !filterIds.get(map.get(key).toString()))
				buff.append("<tr class=\"gray\" style=\"display:table-row;\" >");
			else if (result.size() == 1)
				buff.append("<tr class=\"odd ui-state-default\">");
			else
				buff.append("<tr>");

			if (!deletable && !key.equals("gid"))
				buff.append("<td class=\"noout\">&nbsp;</td>");

			//<c:forEach var="f" varStatus="t" items="${colinfoItems}">
			int t = 0;
			for (TablelistcolumnInfo f : colinfoItems) {
				String hi = "";
				String st = "";
				if (f.highlight!=null && f.highlight) hi = " highlight";
				if (styleMap.get(f.id) != null) {
					//<c:forEach var="g" varStatus="u" items="${styleMap[f.id]}">
					for (TablerowstyleInfo g : styleMap.get(f.id)) {
						if (g.val.equals(map.get(f.attrid)))
							st = g.style;
					}
				}

				//<%-- データ変換 --%>
				Object cval = map.get(f.attrid);
				String colVal = null;
				String imgVal[] = null;
				if (cval != null) colVal = cval.toString();
				if (colVal == null) colVal = "";
				//<%-- 日付形式は yy/mm/dd HH:MM:SS になるように変換する --%>
				if (editClass[t].equals("Date") || editClass[t].equals("DateTime")) {
					colVal = colVal.replace('-', '/');
					colVal = colVal.replace(".0", "");
				}
				if (editClass[t].equals("Date") && colVal.trim().indexOf(" ") >= 0) {
					colVal = colVal.substring(0, colVal.indexOf(" "));
				}
				if (editClass[t].equals("Image")) {
					imgVal = colVal.split("|");
				}
				String st2 = "";
				String tip = "";
				if (editClass[t].equals("String") || editClass[t].equals("TextArea") || editClass[t].equals("Date") || editClass[t].equals("DateTime") || editClass[t].equals("Upload")) {
					st2 = "text-align:"+pageDto.getText_align();
					tip = " showtip";
				}
				else if (editClass[t].equals("Number") || editClass[t].equals("Float"))
					st2 = "text-align:"+pageDto.getNumber_align();

				if (f.editable && !pageDto.isViewMode()) {
					buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+editClass[t]+" "+hi+" "+tip+"\" style=\""+st+" "+st2+"\">");
					if (editClass[t].equals("String") || editClass[t].equals("TextArea") || editClass[t].equals("Number") || editClass[t].equals("Float") || editClass[t].equals("Date") || editClass[t].equals("DateTime"))
						buff.append(colVal);
					else if (editClass[t].equals("Select")) {
						buff.append("<span id=\""+f.attrid+":"+map.get(key)+"\" style=\"display:none;\">"+colVal+"</span>");
						buff.append("<select name=\""+f.attrid+":"+map.get(key)+"\" onChange=\"onSelStyleChange("+f.id+", this, '#"+f.attrid+":"+map.get(key)+"')\">");
						if (!colMap.get(f.attrid) && StringUtil.isNotEmpty(selectVal.get(f.attrid)[0]))
							buff.append("<option value=\"\">");
						int u = 0;
						for (String g : selectStr.get(f.attrid)) {
							String[] g2 = selectVal.get(f.attrid);
							if (!g2[u].equals(colVal)) {
								buff.append("<option value=\""+g2[u]+"\">"+g);
							}
							else {
								buff.append("<option value=\""+g2[u]+"\" selected>"+g);
							}
							u++;
						}
						buff.append("</select>");
					}
					else if (editClass[t].equals("Checkbox")) {
						buff.append("<span id=\""+f.attrid+":"+map.get(key)+"\" style=\"display:none;\">"+colVal+"</span>");
						buff.append("<input type=\"checkbox\" name=\""+f.attrid+":"+map.get(key)+"\" value=\""+checkStr.get(f.attrid)+"\" "+((checkStr.get(f.attrid).equals(colVal))?"checked":"")+" onClick=\"onCheckStyleChange("+f.id+", this, '#"+f.attrid+":"+map.get(key)+"')\">");
					}
					buff.append("</td>");
				}
				else {
					Map<String,String> selMap = selectValView.get(f.attrid);
					if (selMap != null && selMap.size() > 0 && editClass[t].equals("Select")) {
						String val = selMap.get(colVal);
						if (val == null) val = "";
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+" showtip\" style=\""+st+"\">"+val+"</td>");
					}
					else if (editClass[t].equals("Upload")) {
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+"\" style=\""+st+"\">");
						if (colVal.length() > 0)
							buff.append("<a href=\""+request.getContextPath()+colVal+"\" target=\"_blank\">"+colVal+"</a>");
						buff.append("</td>");
					}
					else if (editClass[t].equals("Image")) {
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+"\"><img src=\""+request.getContextPath()+colVal+"\" style=\"max-width:"+photolayerInfo.maximagewidth+"px; max-height:"+photolayerInfo.maximagewidth+"px;\"></td>");
					}
					else if (editClass[t].equals("Date") || editClass[t].equals("DateTime")) {
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+" "+tip+"\" style=\""+st+" "+st2+"\">"+colVal+"</td>");
					}
					else {
						buff.append("<td id=\""+table+":"+f.attrid+":"+key+":"+map.get(key)+"\" class=\""+hi+" "+tip+"\" style=\""+st+" "+st2+"\">"+colVal+"</td>");
					}
				}
				t++;
			}
			buff.append("</tr>");
		}
		buff.append("<script type=\"text/javascript\">");
		buff.append(" nextpage = "+nextpage+";");
		if (!nextpage) {
			buff.append("$('.pagenext').removeClass('ui-state-default');");
			buff.append("$('.pagenext').addClass('ui-state-disabled');");
			buff.append("$('.pageend').removeClass('ui-state-default');");
			buff.append("$('.pageend').addClass('ui-state-disabled');");
		}
		if (nextpage) {
			buff.append("$('.pagenext').removeClass('ui-state-disabled');");
			buff.append("$('.pagenext').addClass('ui-state-default');");
			buff.append("$('.pageend').removeClass('ui-state-disabled');");
			buff.append("$('.pageend').addClass('ui-state-default');");
		}
		if (listForm.npage > 0) {
			buff.append("$('.pageprev').removeClass('ui-state-disabled');");
			buff.append("$('.pageprev').addClass('ui-state-default');");
			buff.append("$('.pagestart').removeClass('ui-state-disabled');");
			buff.append("$('.pagestart').addClass('ui-state-default');");
		}
		if (listForm.npage == 0) {
			buff.append("$('.pageprev').removeClass('ui-state-default');");
			buff.append("$('.pageprev').addClass('ui-state-disabled');");
			buff.append("$('.pagestart').removeClass('ui-state-default');");
			buff.append("$('.pagestart').addClass('ui-state-disabled');");
		}
		buff.append("page = "+listForm.npage+";");
		if (filterDto != null && filterDto.getFilteredFeatureIds() != null)
			buff.append("SaigaiTask.Page.filter.getFilterNumEl().text(\" ( "+filterDto.getFilteredFeatureIds().size()+lang.__("Items")+" / "+filterDto.getTotal()+lang.__("Items")+" )\");");
		int from = pageDto.getPagerow()*listForm.npage+1;
		int to = pageDto.getPagerow()*(listForm.npage+1)+1;
		if (to > count) to = count;
		buff.append("$('.paging-num').text(' ['+addFigure("+from+")+'-'+addFigure("+to+")+'/'+addFigure("+count+")+'"+lang.__("Items")+"　"+(listForm.npage+1)+"/"+listForm.pagesize+lang.__("display page")+"]');");
		buff.append("</script>");

		//ResponseUtil.write(buff.toString());
		return buff.toString();
	}

	/**
	 * ページ表示
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/tablepagebody/{menutaskid}/{menuid}/{npage}")
	@ResponseBody
	public String tablepagebody(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm){

		return tablebody(model, listForm);
	}

	/**
	 * ページ表示
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/tablepagebodysort/{menutaskid}/{menuid}/{npage}/{sort}")
	@ResponseBody
	public String tablepagebodysort(Map<String,Object>model, @Valid @ModelAttribute ListForm listForm){

		return tablebody(model, listForm);
	}
}
