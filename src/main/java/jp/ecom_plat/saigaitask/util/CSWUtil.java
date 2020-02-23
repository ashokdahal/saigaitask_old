/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.ecom_plat.map.util.StringUtils;
import jp.ecom_plat.saigaitask.action.ServiceException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.OperationType;
import org.geotools.data.ows.Service;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.response.WMSGetCapabilitiesResponse;
import org.jdom2.Element;
import org.seasar.framework.util.StringUtil;

/**
 * 災害リスク情報クリアリングハウスへのインタフェースを提供するクラス
 * メソッドに cswurl の指定がない場合は デフォルトのクリアリングハウス{@link CSWUtil#cswurl}が使用される。
 */
public class CSWUtil {
    static final Logger logger = Logger.getLogger(CSWUtil.class);
    // シングルトンインスタンス
    //private static final CSWUtil csw = new CSWUtil();
    /** デフォルトの CSWサーバのURL */
    private static String cswurl;
    /** デフォルトの CSWサーバのユーザ名 */
    private static String cswuser;
    /** デフォルトの CSWサーバのパスワード */
    private static String cswpasswd;
    static {
    	// デフォルトのクリアリングハウスの初期化
    	CSWUtil.cswurl = Config.getString("CSWURL");
    	logger.debug("Default CSWURL: "+CSWUtil.cswurl);
    	CSWUtil.cswuser = Config.getString("CSWUSER");
    	logger.debug("Default CSWUSER: "+CSWUtil.cswuser);
    	CSWUtil.cswpasswd = Config.getString("CSWPASSWD");
    	logger.debug("Default CSWPASSWD: "+CSWUtil.cswpasswd);
    }
    // スキーマ
	public static final String SCHEMA_CSW = "http://www.opengis.net/cat/csw/2.0.2";
	public static final String SCHEMA_GMD = "http://www.isotc211.org/2005/gmd";
	public static final String SCHEMA_WMS = "http://www.opengis.net/wms";
	public static final String SCHEMA_WFS = "http://www.opengis.net/wfs"; // 1.0.0,1.1.0
	public static final String SCHEMA_WCS = "http://www.opengis.net/wcs"; // 1.0.0,
	public static final String SCHEMA_WCS11 = "http://www.opengis.net/wcs/1.1"; // 1.1.0,1.1.2,1.1.3,1.1
	public static final String SCHEMA_WCS111 = "http://www.opengis.net/wcs/1.1.1"; // 1.1.1,
	public static final String SCHEMA_JMP = "http://zgate.gsi.go.jp/ch/jmp";
	public static final String SCHEMA_JMPex = "http://schemas.info-bosai.jp/ch/jmpex";

    /**
     * メタデータ検索
     * デフォルトのクリアリングハウスに GetRecords リクエストを送信してメタデータを検索する
     *
     * @param condition 検索条件、パラメータ名と値のMap
     * @param startpos 検索結果取得開始位置、1 から
     * @param maxrec 検索結果取得最大件数
     * @param orderby 並び替え 1:日付（識別情報）降順 2:アクセス数降順 3:評価降順 5:検索条件との一致度降順
     * @return 検索結果（GetRecordsResponse XML）
     */
    public static String getRecords(Map<String,String> condition,int startpos,int maxrec,int orderby){
    	return getRecords(CSWUtil.cswurl, CSWUtil.cswuser, CSWUtil.cswpasswd, condition, startpos, maxrec, orderby);
    }
    /**
     * メタデータ検索
     * 指定したクリアリングハウスに GetRecords リクエストを送信してメタデータを検索する
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param condition 検索条件、パラメータ名と値のMap
     * @param startpos 検索結果取得開始位置、1 から
     * @param maxrec 検索結果取得最大件数
     * @param orderby 並び替え 1:日付（識別情報）降順 2:アクセス数降順 3:評価降順 5:検索条件との一致度降順
     * @return 検索結果（GetRecordsResponse XML）
     */
    public static String getRecords(String cswurl, String user, String passwd, Map<String,String> condition,int startpos,int maxrec,int orderby){
        try{
            StringBuffer q = new StringBuffer();
            q.append("SERVICE=CSW");
            q.append("&VERSION=");
            q.append(urlencode("2.0.2"));
            q.append("&REQUEST=GetRecords");
            q.append("&RESULTTYPE=results");
            q.append("&OUTPUTFORMAT=");
            q.append(urlencode("application/xml"));
            q.append("&OUTPUTSCHEMA=");
            q.append(SCHEMA_CSW);
            q.append("&ELEMENTSETNAME=full");
            q.append("&STARTPOSITION=");
            q.append(startpos);
            q.append("&MAXRECORDS=");
            q.append(maxrec);
            q.append("&ORDERBY=");
            q.append(orderby);
            q.append("&enc=UTF-8");
            // 災害区分
            if(condition.get("DISASTERTYPE") != null){
                q.append("&DISASTERTYPE=");
                q.append(urlencode(condition.get("DISASTERTYPE")));
            }
            // キーワード（AND）
            if(condition.get("KEYWORD") != null){
                q.append("&KEYWORD=");
                q.append(urlencode(condition.get("KEYWORD")));
            }
            // キーワード（OR）
            if(condition.get("KEYWORDOR") != null){
                q.append("&KEYWORDOR=");
                q.append(urlencode(condition.get("KEYWORDOR")));
            }
            // キーワード（NOT）
            if(condition.get("KEYWORDNOT") != null){
                q.append("&KEYWORDNOT=");
                q.append(urlencode(condition.get("KEYWORDNOT")));
            }
            // あいまい検索
            if(condition.get("FUZZYSEARCH") != null){
                q.append("&FUZZYSEARCH=");
                q.append(urlencode(condition.get("FUZZYSEARCH")));
            }
            // 問い合わせ先名称（組織名）
            if(condition.get("CONTACTNAME") != null){
                q.append("&CONTACTNAME=");
                q.append(urlencode(condition.get("CONTACTNAME")));
            }
            // 範囲（地理境界ボックス）
            if(condition.get("BBOX") != null){
                q.append("&BBOX=");
                q.append(urlencode(condition.get("BBOX")));
            }
            // 日付（識別情報）と日付区分
            if(condition.get("TERM") != null){
                q.append("&TERM=");
                q.append(urlencode(condition.get("TERM")));
            }
            if(condition.get("TERMTYPE") != null){
                q.append("&TERMTYPE=");
                q.append(urlencode(condition.get("TERMTYPE")));
            }
            // 日付（時間範囲）
            if(condition.get("DATATERM") != null){
                q.append("&DATATERM=");
                q.append(urlencode(condition.get("DATATERM")));
            }
            // 評価
            if(condition.get("RATING") != null){
                q.append("&RATING=");
                q.append(urlencode(condition.get("RATING")));
            }
            // WMS,WFS,WCS等
            if(condition.get("APPLICATIONPROFILE") != null){
                q.append("&APPLICATIONPROFILE=");
                q.append(urlencode(condition.get("APPLICATIONPROFILE")));
            }
            // メタデータの更新日時（システムが管理）による検索
            if(condition.get("UPDATETERM") != null){
                q.append("&UPDATETERM=");
                q.append(urlencode(condition.get("UPDATETERM")));
            }
            // 予定メタデータを含む
            if(condition.get("INCLUDEPLANNED") != null){
                q.append("&INCLUDEPLANNED=");
                q.append(urlencode(condition.get("INCLUDEPLANNED")));
            }
			// 地図データ、サービスのURL
			if(condition.get("RESOURCEURL") != null){
				q.append("&RESOURCEURL=");
				q.append(urlencode(condition.get("RESOURCEURL")));
			}
			// カスケード検索設定
			if(condition.get("DISTRIBUTEDSEARCH") != null){
				q.append("&DISTRIBUTEDSEARCH=");
				q.append(urlencode(condition.get("DISTRIBUTEDSEARCH")));
			}
			//HOPCOUNTは分散検索する階層の指定で、次のサーバにリクエストする際に-1して渡します。
			//HOPCOUNT=1だと自分の検索して終わりです。
			//大きい値を指定しても問題ないですが、分散検索の設定していなければHOPCOUNTが残っていても終わりです。
			if(condition.get("HOPCOUNT") != null){
				q.append("&HOPCOUNT=");
				q.append(urlencode(condition.get("HOPCOUNT")));
			}
            StringBuffer sb = new StringBuffer();
            int st = sendHttpRequest(cswurl, user, passwd, q.toString(),"GET",sb);
            if(st == HttpURLConnection.HTTP_OK)
                return sb.toString();
        }finally{
        }
        return null;
    }

    /**
     * メタデータ取得
     * デフォルトのクリアリングハウスに GetRecordById リクエストを送信してメタデータを取得する。
     *
     * @param id メタデータのファイル識別子
     * @return メタデータ（MD_Metadata XML）
     */
    public static String getRecordById(String id){
        return getRecordById(CSWUtil.cswurl,CSWUtil.cswuser,CSWUtil.cswpasswd,id);
    }
    /**
     * メタデータ取得
     * 指定したクリアリングハウスに GetRecordById リクエストを送信してメタデータを取得する。
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param id メタデータのファイル識別子
     * @return メタデータ（MD_Metadata XML）
     */
    public static String getRecordById(String cswurl, String user, String passwd, String id){
        return getRecordById(cswurl,user,passwd,id,SCHEMA_JMPex);
    	//return getRecordById(id,SCHEMA_WMS);
    }

    /**
     * メタデータ取得
     * クリアリングハウスに GetRecordById リクエストを送信してメタデータを取得する。
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param id メタデータのファイル識別子
     * @param schema レスポンスのスキーマ定義
     * @return メタデータ（MD_Metadata XML）
     */
    public static String getRecordById(String cswurl, String user, String passwd,String id,String schema){
        try{
            String q =
                "SERVICE=CSW"+
                "&VERSION="+urlencode("2.0.2")+
                "&REQUEST=GetRecordById"+
                "&ELEMENTSETNAME=full"+
                "&OUTPUTFORMAT="+urlencode("application/xml")+
                "&OUTPUTSCHEMA="+urlencode(schema)+
                "&Id="+urlencode(id);
            StringBuffer sb = new StringBuffer();
            int st = sendHttpRequest(cswurl,user,passwd,q,"GET",sb);
            if(st == HttpURLConnection.HTTP_OK)
                return sb.toString();
        }finally{
        }
        return null;
    }




    /**
     * メタデータ登録・更新
     * デフォルトのクリアリングハウスに UploadMetadata リクエストを送信してメタデータを登録する。
     * ファイル識別子が一致するメタデータが登録済みならば更新となる
     *
     * @param xml メタデータ（MD_Metadata XML テキスト)
     * @param thumbnail サムネイル画像
     * @param thumbfname サムネイル画像のファイル名
     * @return 登録・更新結果、true/false
     */
    public static boolean uploadMetadata(String xml,byte[] thumbnail,String thumbfname){
        return uploadMetadata(CSWUtil.cswurl,CSWUtil.cswuser,CSWUtil.cswpasswd, xml,thumbnail,thumbfname);
    }
    /**
     * メタデータ登録・更新
     * 指定したクリアリングハウスに UploadMetadata リクエストを送信してメタデータを登録する。
     * ファイル識別子が一致するメタデータが登録済みならば更新となる
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param xml メタデータ（MD_Metadata XML テキスト)
     * @param thumbnail サムネイル画像
     * @param thumbfname サムネイル画像のファイル名
     * @return 登録・更新結果、true/false
     */
    public static boolean uploadMetadata(String cswurl, String user, String passwd, String xml,byte[] thumbnail,String thumbfname){
        return uploadMetadata(cswurl,user,passwd, xml,thumbnail,thumbfname,MetadataUtil.DT_UNCLASSIFIED,MetadataUtil.OM_PUBLIC,null,null);
    }

    /**
     * メタデータ登録・更新
     * 指定したクリアリングハウスに UploadMetadata リクエストを送信してメタデータを登録する。
     * ファイル識別子が一致するメタデータが登録済みならば更新となる
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param xml メタデータ（MD_Metadata XML テキスト)
     * @param thumbnail サムネイル画像
     * @param thumbfname サムネイル画像のファイル名
     * @param disastertype 災害区分
     * @param openmode 公開モード
     * @param openmodegrp 公開対象グループのID、カンマ区切り、グループ公開のみ必須
     * @param redirecturl 登録後のリダイレクト先のURL
     * @param 登録・更新結果、true/false
     */
    public static boolean uploadMetadata(String cswurl, String user, String passwd, String xml,byte[] thumbnail,String thumbfname,String disastertype,String openmode,String openmodegrp,String redirecturl){
        // パラメータ
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("SERVICE","CSW");
        params.put("VERSION","2.0.2");
        params.put("REQUEST","UploadMetadata");
        if(disastertype == null)
            disastertype = MetadataUtil.DT_UNCLASSIFIED;
        params.put("DISASTERTYPE",disastertype);
        if(openmode == null)
            openmode = MetadataUtil.OM_PUBLIC;
        params.put("OPENMODE",openmode);
        if(openmodegrp != null)
            params.put("OPENMODEGRP",openmodegrp);
        if(redirecturl != null)
            params.put("REDIRECTURL",redirecturl);
        // メタデータとサムネイル
        HashMap<String,byte[]> datas = new HashMap<String,byte[]>();
        HashMap<String,String> fnames = new HashMap<String,String>();
		try{
			datas.put("METADATAFILE",xml.getBytes("UTF-8"));
		}catch(java.io.UnsupportedEncodingException e){
			// ignore
		}
        fnames.put("METADATAFILE","md"+new SimpleDateFormat("yyyyMMdd").format(new java.util.Date())+".xml");
        if(thumbnail != null){
            datas.put("OVERVIEWFILE",thumbnail);
            if(thumbfname != null)
                fnames.put("OVERVIEWFILE",thumbfname);
        }

        StringBuffer sb = new StringBuffer();
        int st = sendHttpMultipartRequest(cswurl,user,passwd, params,datas,fnames,sb);
        if(st == HttpURLConnection.HTTP_OK){
            logger.debug("uploadMetadata response: "+sb.toString());
            Element elm = MetadataUtil.parseMetadata(sb.toString());
            if(elm != null && "ExceptionReport".equals(elm.getName())) {
            	throw new ServiceException(MetadataUtil.getExceptionText(elm));
                //return false;
            }
            return true;
        }
        return false;
    }

    /**
     * メタデータ削除
     * クリアリングハウスに DeleteMetadata リクエストを送信してメタデータを削除する。
     *
     * @param id 削除するメタデータのファイル識別子
     * @return 削除結果、true/false
     */
    public static boolean deleteMetadata(String id){
    	return deleteMetadata(CSWUtil.cswurl, CSWUtil.cswuser, CSWUtil.cswpasswd, id);
    }

    /**
     * メタデータ削除
     * クリアリングハウスに DeleteMetadata リクエストを送信してメタデータを削除する。
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param id 削除するメタデータのファイル識別子
     * @return 削除結果、true/false
     */
    public static boolean deleteMetadata(String cswurl, String user, String passwd, String id){
        String q = "SERVICE=CSW&VERSION=2.0.2&REQUEST=DeleteMetadata&FILEIDENTIFIER="+urlencode(id);
        StringBuffer sb = new StringBuffer();
        int st = sendHttpRequest(cswurl, user, passwd, q,"GET",sb);
        if(st == HttpURLConnection.HTTP_OK){
            logger.debug("deleteMetadata response: "+sb.toString());
            Element elm = MetadataUtil.parseMetadata(sb.toString());
            if(elm != null && "ExceptionReport".equals(elm.getName()))
                return false;
            return true;
        }
        return false;
    }

    /**
     * メタデータ（MD_Metadata）を JSON に変換
     *
     * @param xml メタデータのXMLテキスト
     * @return JSON化されたメタデータ
     */
    public static String MDMetadataToJSON(String xml){
        Element elm = MetadataUtil.parseMetadata(xml);
        if(elm != null && MetadataUtil.TAG_MDMetadata.equals(elm.getName())){
            LinkedHashMap<String,Object> items = new LinkedHashMap<String,Object>();
            String mid = elm.getAttributeValue("identifier");
            items.put("metadataId",mid);
            String updtime = elm.getAttributeValue("updateTime");
            items.put("updateTime",updtime);
            String fid = MetadataUtil.getFileIdentifier(elm);
            items.put("fileIdentifier",fid);
            String[] useLimit = MetadataUtil.getUseLimitation(elm);
            items.put("useLimit",useLimit);
            String title = MetadataUtil.getTitle(elm);
            items.put("title",title);
            String abst = MetadataUtil.getAbstract(elm);
            items.put("abstract",abst);
            String purpose = MetadataUtil.getPurpose(elm);
            items.put("purpose",purpose);
            String[][] thumbs = MetadataUtil.getBrowseGraphic(elm);
            items.put("thumbnail",thumbs!=null?thumbs[0][0]:null);
            String[] wmsurl = MetadataUtil.getWMSInfo(elm);
            if(wmsurl != null){
                items.put("wmsurl",wmsurl[0]);
                items.put("wmsparam",wmsurl[1]);
                items.put("wmslayer",wmsurl[2]);
            }
            String[] wfsurl =MetadataUtil.getWFSInfo(elm);
            if(wfsurl != null){
                items.put("wfsurl",wfsurl[0]);
                items.put("wfsparam",wfsurl[1]);
                items.put("wfslayer",wfsurl[2]);
            }
            String[] xyzurl = MetadataUtil.getServiceInfo("XYZ", elm);
            if(xyzurl != null){
                items.put("xyzurl",xyzurl[0]);
                items.put("xyzparam",xyzurl[1]);
                items.put("xyzlayer",xyzurl[2]);
                items.put("xyzopts",xyzurl[2]);
                items.put("xyzdesc",xyzurl[4]); // XYZオプション
            }
            // メタデータ作成者問い合わせ先(ロール指定なし)
            {
                String contact = MetadataUtil.getOrganisationName(elm);
                items.put("contactname",contact);
                String city = MetadataUtil.getCity(elm);
                items.put("city",city);
                String administartiveArea = MetadataUtil.getAdministrativeArea(elm);
                items.put("administartiveArea",administartiveArea);
                String cityCode = MetadataUtil.getCityCode(elm);
                items.put("cityCode",cityCode);
                String administartiveAreaCode = MetadataUtil.getAdministrativeAreaCode(elm);
                items.put("adminAreaCode",administartiveAreaCode);	
            }
			// 地図データ問い合わせ先(ロール指定あり)
			for(String role : new String[]{MetadataUtil.CI_RoleCode.POINT_OF_CONTACT}) {
				String prefix = role.equals(MetadataUtil.CI_RoleCode.POINT_OF_CONTACT) ? "Ident" : "MapContact"+role;
				items.put(prefix+"_role", role);
				// 識別情報問い合わせ先（組織名）（contact/organisationName）
				items.put(prefix+"_rpOrgName", MetadataUtil.getOrganisationName(role, elm));
				// 識別情報問い合わせ先（電話番号）（contact/contactInfo/phone/voice）
				items.put(prefix+"_cntPhone", MetadataUtil.getPhone(role, elm));
				// 問い合わせ先（住所詳細）（contact/contactInfo/address/deliveryPoint）
				items.put(prefix+"_delPoint", MetadataUtil.getDeliveryPoint(role, elm));
				// 問い合わせ先（市区町村）（contact/contactInfo/address/city）
				items.put(prefix+"_city", MetadataUtil.getCity(role, elm));
				// 問い合わせ先（市区町村コード）（contact/contactInfo/address/cityCode）
				items.put(prefix+"_cityCode", MetadataUtil.getCityCode(role, elm));
				// 問い合わせ先（都道府県名）（contact/contactInfo/address/administrativeArea）
				items.put(prefix+"_adminArea", MetadataUtil.getAdministrativeArea(role, elm));
				// 問い合わせ先（都道府県コード）（contact/contactInfo/address/administrativeAreaCode）
				items.put(prefix+"_adminAreaCode", MetadataUtil.getAdministrativeAreaCode(role, elm));
				// 問い合わせ先（郵便番号）（contact/contactInfo/address/postalCode）
				items.put(prefix+"_postCode", MetadataUtil.getPostalCode(role, elm));
				// 問い合わせ先（電子メール）（contact/contactInfo/address/electronicMailAddress）
				items.put(prefix+"_eMailAdd", MetadataUtil.getEMailAddress(role, elm));
				// 問い合わせ先（リンク）（contact/contactInfo/onlineResource/linkage）
				items.put(prefix+"_linkage", MetadataUtil.getLinkage(role, elm));
			}
			try {
				// 時間に関する情報
				/* 例
				<temporalElement>
					<EX_TemporalExtent>
						<extent>
							<beginEnd>
								<begin>8994-08-17T04:25:51Z</begin>
								<end>2016-10-06T16:32:36Z</end>
							</beginEnd>
						</extent>
					</EX_TemporalExtent>
				</temporalElement>
				*/
				// temporalElement は１件しか設定されないはず
				List<Element> temporalElements = MetadataUtil.getTemporalElement(elm);
				if(temporalElements!=null && 0<temporalElements.size()) {
					Element exTemporalElement = temporalElements.get(0);
					Element beginEnd = MetadataUtil.getXMLElement(new String[]{"EX_TemporalExtent", "extent", "beginEnd"}, exTemporalElement, MetadataUtil.jmpPrefix);
					if(beginEnd!=null) {
						String begin = MetadataUtil.getXMLValue(new String[]{"begin"}, beginEnd, MetadataUtil.jmpPrefix);
						String end = MetadataUtil.getXMLValue(new String[]{"end"}, beginEnd, MetadataUtil.jmpPrefix);
						String[] exTemp = new String[]{begin, end};
						items.put("exTemp", exTemp);
					}
				}
			} catch(Exception e) {
				logger.error("Faild to get temporalExtent", e);
			}
            return mapToJSON(items);
        }
        return "{}";
    }

    /**
     * 検索結果（GetRecordsReponse）を JSON に変換
     *
     * @param xml 検索結果のXMLテキスト
     * @return JSON化された検索結果
     */
    public static String GetRecordsResponseToJSON(String xml){
        Element elm = MetadataUtil.parseMetadata(xml);
        if(elm != null && "GetRecordsResponse".equals(elm.getName())){
            LinkedHashMap<String,Object> items = new LinkedHashMap<String,Object>();
            String recid = MetadataUtil.getXMLValue(new String[]{"RequestId"},elm,MetadataUtil.cswPrefix);
            items.put("requestId",recid);
            List<Element> results = MetadataUtil.getXMLElements(new String[]{"SearchResults"},elm,MetadataUtil.cswPrefix);
            String nummatch = results.get(0).getAttributeValue("numberOfRecordsMatched");
            items.put("numMatch",nummatch);
            String numret = results.get(0).getAttributeValue("numberOfRecordsReturned");
            items.put("numReturn",numret);
            String nextrec = results.get(0).getAttributeValue("nextRecord");
            items.put("nextRecord",nextrec);
            List<Element> recs = MetadataUtil.getXMLElements(new String[]{"Record"},results.get(0),MetadataUtil.cswPrefix);
            if(recs != null){
                ArrayList<LinkedHashMap<String,Object>> recarr = new ArrayList<LinkedHashMap<String,Object>>();
                for(Element e  : recs){
                    LinkedHashMap<String,Object> rec = new LinkedHashMap<String,Object>();
                    String mid = MetadataUtil.getXMLValue(new String[]{"dc:identifier"},e,MetadataUtil.cswPrefix);
                    rec.put("metadataId",mid);
                    String updtime = e.getAttributeValue("updateTime");
                    rec.put("updateTime",updtime);
                    String fid = e.getAttributeValue("fileIdentifier");
                    rec.put("fileIdentifier",fid);
                    String originalSource = e.getAttributeValue("originalSource");
                    rec.put("originalSource",originalSource);
                    String contact = MetadataUtil.getXMLValue(new String[]{"dc:publisher"},e,MetadataUtil.cswPrefix);
                    rec.put("contactname",contact);
                    String title = MetadataUtil.getXMLValue(new String[]{"dc:title"},e,MetadataUtil.cswPrefix);
                    rec.put("title",title);
                    String abst = MetadataUtil.getXMLValue(new String[]{"dct:abstract"},e,MetadataUtil.cswPrefix);
                    rec.put("abstract",abst);
                    String[] wmsinfo = MetadataUtil.getXMLValues(new String[]{"AnyText"},e,MetadataUtil.cswPrefix);
                    if(wmsinfo != null){
                        for(String w : wmsinfo){
                            if(w.startsWith("nied:thumbnail ")){
                                rec.put("thumbnail",w.substring("nied:thumbnail ".length()));
                            }else if(w.startsWith("nied:wmsurl ")){
                                rec.put("wmsurl",w.substring("nied:wmsurl ".length()));
                            }else if(w.startsWith("nied:wmslayers ")){
                                rec.put("wmslayer",w.substring("nied:wmslayer ".length()));
                            }
                        }
                    }
                    recarr.add(rec);
                }
                items.put("record",(LinkedHashMap<String,Object>[])recarr.toArray(new LinkedHashMap<?,?>[recarr.size()]));
            }else{
                items.put("record",null);
            }
            return mapToJSON(items);
        }
        return "{}";
    }

    /**
     * メタデータ（MD_Metadata XML テキスト）を作成
     *
     * @param items メタデータ項目
     * @return MD_Metadata XML テキスト
     */
    public static String constructMetadata(Map<String,Object> items){
        Element elm = MetadataUtil.createMetadata();
        String val;
        String[] vals;
		boolean isPlanned = MetadataUtil.MD_ProgressCode.EXPECTED.equals((String)items.get("idStatus")); // 予定メタデータかどうか
        // ファイル識別子（fileIdentifier）
        val = (String)items.get("mdFileID");
        if(val != null)
            MetadataUtil.setFileIdentifier(val,elm);
        // 言語（language/isoCode）
        val = (String)items.get("mdLang");
        if(val == null)
            val = "jpn";
        MetadataUtil.setLanguage(val,elm);
        // 文字集合（CharacterSet）
        val = (String)items.get("mdChar");
        if(val == null)
            val = MetadataUtil.MD_CharacterSetCode.UTF_8;
        MetadataUtil.setCharacterSet(val,elm);
        // 階層レベル（hierarchyLevel）
        vals = (String[])items.get("mdHrLv");
        if(vals == null)
            vals = new String[]{MetadataUtil.MD_ScopeCode.DATASET};
        MetadataUtil.setHierarchyLevel(vals,elm);
        // 問い合わせ先（組織名）（contact/organisationName）
        val = (String)items.get("mdContact_rpOrgName");
        if(val != null)
            MetadataUtil.setOrganisationName(val,elm);
        // 問い合わせ先（電話番号）（contact/contactInfo/phone/voice）
        vals = (String[])items.get("mdContact_cntPhone");
        if(vals != null)
            MetadataUtil.setPhone(vals,elm);
        // 問い合わせ先（住所詳細）（contact/contactInfo/address/deliveryPoint）
        vals = (String[])items.get("mdContact_delPoint");
        if(vals != null)
            MetadataUtil.setDeliveryPoint(vals,elm);
        // 問い合わせ先（市区町村）（contact/contactInfo/address/city）
        val = (String)items.get("mdContact_city");
        if(val != null)
            MetadataUtil.setCity(val,elm);
        // 問い合わせ先（市区町村コード）（contact/contactInfo/address/cityCode）
        val = (String)items.get("mdContact_cityCode");
        if(val != null)
            MetadataUtil.setCityCode(val,elm);
        // 問い合わせ先（都道府県名）（contact/contactInfo/address/administrativeArea）
        val = (String)items.get("mdContact_adminArea");
        if(val != null)
            MetadataUtil.setAdministrativeArea(val,elm);
        // 問い合わせ先（都道府県コード）（contact/contactInfo/address/administrativeAreaCode）
        val = (String)items.get("mdContact_adminAreaCode");
        if(val != null)
            MetadataUtil.setAdministrativeAreaCode(val,elm);
        // 問い合わせ先（郵便番号）（contact/contactInfo/address/postalCode）
        val = (String)items.get("mdContact_postCode");
        if(val != null)
            MetadataUtil.setPostalCode(val,elm);
        // 問い合わせ先（国）（contact/contactInfo/address/country）
        val = (String)items.get("mdContact_country");
        if(val == null)
            val = "jpn";
        MetadataUtil.setCountry(val,elm);
        // 問い合わせ先（電子メール）（contact/contactInfo/address/electronicMailAddress）
        vals = (String[])items.get("mdContact_eMailAdd");
        if(vals != null)
            MetadataUtil.setEMailAddress(vals,elm);
        // 問い合わせ先（リンク）（contact/contactInfo/onlineResource/linkage）
        val = (String)items.get("mdContact_linkage");
        if(val != null)
            MetadataUtil.setLinkage(val,elm);
        // メタデータの日付（dateStamp）
        val = (String)items.get("mdDateSt");
		if(isPlanned)
			val = "2999-12-31";	// 予定メタデータは固定日付
        if(val != null)
            MetadataUtil.setDateStamp(val,elm);
        // メタデータの規格の名称（metadataStandardName）
        val = (String)items.get("mdStanName");
        if(val == null)
            val = "JMP";
        MetadataUtil.setMetadataStandardName(val,elm);
        // メタデータの規格のバージョン（metadataStandardVersion）
        val = (String)items.get("mdStanVer");
        if(val == null)
            val = "2.0";
        MetadataUtil.setMetadataStandardVersion(val,elm);
        // タイトル（identificationInfo/MD_DataIdentification/citation/title）
        val = (String)items.get("resTitle");
        if(val != null)
            MetadataUtil.setTitle(val,elm);
        // 日付-作成日（識別情報）(identificationInfo/MD_DataIdentification/citation/date/date）
        val = (String)items.get("refDateCrt");
        if(val != null)
            MetadataUtil.setIdentificationDate(val,MetadataUtil.CI_DateTypeCode.CREATION,elm);
        // 日付-刊行日（識別情報）(identificationInfo/MD_DataIdentification/citation/date/date）
        val = (String)items.get("refDatePub");
        if(val != null)
            MetadataUtil.setIdentificationDate(val,MetadataUtil.CI_DateTypeCode.PUBLICATION,elm);
        // 日付-改訂日（識別情報）(identificationInfo/MD_DataIdentification/citation/date/date）
        val = (String)items.get("refDateRev");
        if(val != null)
            MetadataUtil.setIdentificationDate(val,MetadataUtil.CI_DateTypeCode.REVISION,elm);
        // 要約（identificationInfo/MD_DataIdentification/abstract）
        val = (String)items.get("idAbs");
        if(val != null)
            MetadataUtil.setAbstract(val,elm);
        // 目的（identificationInfo/MD_DataIdentification/purpose）
        val = (String)items.get("idPurp");
        if(val != null)
            MetadataUtil.setPurpose(val,elm);
        // 状態（完成:001 または予定：999）（identificationInfo/MD_DataIdentification/status）
        val = (String)items.get("idStatus");
        if(val != null)
            MetadataUtil.setStatus(val,elm);
        String role = (String)items.get("Ident_role");// （identificationInfo/MD_DataIdentification/pointOfContact/role）
        if(role == null)
            role = MetadataUtil.CI_RoleCode.POINT_OF_CONTACT;
        // 識別情報問い合わせ先（組織名）（identificationInfo/MD_DataIdentification/pointOfContact/organisationName）
        val = (String)items.get("Ident_rpOrgName");
        if(val != null)
            MetadataUtil.setOrganisationName(role,val,elm);
        // 識別情報問い合わせ先（電話番号）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/phone/voice）
        vals = (String[])items.get("Ident_cntPhone");
        if(vals != null)
            MetadataUtil.setPhone(role,vals,elm);
        // 識別情報問い合わせ先（住所詳細）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/deliveryPoint）
        vals = (String[])items.get("Ident_delPoint");
        if(vals != null)
            MetadataUtil.setDeliveryPoint(role,vals,elm);
        // 識別情報問い合わせ先（市区町村）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/city）
        val = (String)items.get("Ident_city");
        if(val != null)
            MetadataUtil.setCity(role,val,elm);
        // 識別情報問い合わせ先（市区町村コード）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/cityCode）
        val = (String)items.get("Ident_cityCode");
        if(val != null)
            MetadataUtil.setCityCode(role,val,elm);
        // 識別情報問い合わせ先（都道府県名）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/administrativeArea）
        val = (String)items.get("Ident_adminArea");
        if(val != null)
            MetadataUtil.setAdministrativeArea(role,val,elm);
        // 識別情報問い合わせ先（都道府県コード）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/administrativeAreaCode）
        val = (String)items.get("Ident_adminAreaCode");
        if(val != null)
            MetadataUtil.setAdministrativeAreaCode(role,val,elm);
        // 識別情報問い合わせ先（郵便番号）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/postalCode）
        val = (String)items.get("Ident_postCode");
        if(val != null)
            MetadataUtil.setPostalCode(role,val,elm);
        // 識別情報問い合わせ先（国）（デフォルト）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/country）
        val = (String)items.get("Ident_country");
        if(val == null)
            val = "jpn";
        MetadataUtil.setCountry(role,val,elm);
        // 識別情報問い合わせ先（電子メール）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/electronicMailAddress）
        vals = (String[])items.get("Ident_eMailAdd");
        if(vals != null)
            MetadataUtil.setEMailAddress(role,vals,elm);
        // 識別情報問い合わせ先（リンク）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/onlineResource/linkage）
        val = (String)items.get("Ident_linkage");
        if(val != null)
            MetadataUtil.setLinkage(role,val,elm);
        // サムネイル（identificationInfo/MD_DataIdentification/graphicOverview/MD_BrowseGraphic/fileName）
        val = (String)items.get("bgFileName");
        if(val != null)
            MetadataUtil.setBrowseGraphic(val,null,null,elm);
        // キーワード（identificationInfo/MD_DataIdentification/descriptiveKeywords/MD_Keywords/keyword,type）
        String kt = (String)items.get("keyTyp");
        if(kt == null)
            kt = MetadataUtil.MD_KeywordTypeCode.THEME;
        vals = (String[])items.get("Keyword");
        if(vals != null)
            MetadataUtil.setKeywords(kt,vals,elm);
        // 利用制限（identificationInfo/MD_DataIdentification/resourceConstraints/MD_Constraints/useLimitation）
        vals = (String[])items.get("useLimit");
        if(vals != null)
            MetadataUtil.setUseLimitation(vals,elm);
        // 識別情報言語（identificationInfo/MD_DataIdentification/language/isoCode）
        val = (String)items.get("dataLang");
        if(val == null)
            val = "jpn";
        MetadataUtil.setDataLanguage(val,elm);
        // 識別情報文字集合（identificationInfo/MD_DataIdentification/characterSet）
        val = (String)items.get("dataChar");
        if(val == null)
            val = MetadataUtil.MD_CharacterSetCode.UTF_8;
        MetadataUtil.setDataCharacterSet(val,elm);
        // 主題分類（identificationInfo/MD_DataIdentification/topicCategory）
        vals = (String[])items.get("tpCat");
        if(vals != null)
            MetadataUtil.setTopicCategory(vals,elm);
        // 地理境界ボックス（identificationInfo/MD_DataIdentification/extent/geographicElement/EX_GeographicBoundingBox/westBoundLongitude,eastBoundLongitude,southBoundLatitude,northBoundLatitude）
        vals = (String[])items.get("HoriBndBox");
        if(vals != null)
            MetadataUtil.setGeographicBoundingBox("WGS84/(B,L)",Double.parseDouble(vals[0]),Double.parseDouble(vals[1]),Double.parseDouble(vals[2]),Double.parseDouble(vals[3]),elm);
        // 時間要素瞬間（identificationInfo/MD_DataIdentification/extent/temporalElement/EX_TemporalExtent/extent/instant）
        val = (String)items.get("instTemp");
        if(val != null)
            MetadataUtil.setTemporalElement(val,elm);
        // 時間要素範囲（identificationInfo/MD_DataIdentification/extent/temporalElement/EX_TemporalExtent/extent/beginEnd/begin,end）
        vals = (String[])items.get("exTemp");
        if(vals != null)
            MetadataUtil.setTemporalElement(vals[0],vals[1],elm);
        // 配布情報（WMS）（distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/linkage）
        vals = (String[])items.get("onlineSrc_WMS");
        if(vals != null)
            MetadataUtil.addDataAccessURL("WMS",null,vals[0],"http","WMS"+(vals.length>1&&vals[1]!=null?":"+vals[1]:""),null,null,null,elm);
        // 配布情報（WFS）（distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/linkage）
        vals = (String[])items.get("onlineSrc_WFS");
        if(vals != null)
            MetadataUtil.addDataAccessURL("WFS",null,vals[0],"http","WFS"+(vals.length>1&&vals[1]!=null?":"+vals[1]:""),null,null,null,elm);
        return MetadataUtil.ElementToXML(elm,true);
    }

    /**
     * メタデータ（MD_Metadata XML テキスト）を更新
     * 値がセットされてない項目のみ
     *
	 * @param xml メタデータ（MD_Metadata XML テキスト）
     * @param items メタデータ項目
     * @return MD_Metadata XML テキスト
     */
    public static String replaceMetadataIfEmpty(String xml,Map<String,Object> items){
    	return replaceMetadata(xml, items, /*replaceIfEmpty*/true);
    }
    /**
     * メタデータ（MD_Metadata XML テキスト）を更新
     *
	 * @param xml メタデータ（MD_Metadata XML テキスト）
     * @param items メタデータ項目
     * @return MD_Metadata XML テキスト
     */
    public static String replaceMetadata(String xml,Map<String,Object> items){
    	return replaceMetadata(xml, items, /*replaceIfEmpty*/false);
    }
    /**
     * メタデータ（MD_Metadata XML テキスト）を更新
     *
	 * @param xml メタデータ（MD_Metadata XML テキスト）
     * @param items メタデータ項目
     * @param replaceIfEmpty 値がセットされてない項目のみ
     * @return MD_Metadata XML テキスト
     */
    public static String replaceMetadata(String xml,Map<String,Object> items, boolean replaceIfEmpty){
		Element elm = MetadataUtil.parseMetadata(xml);
		if(elm == null || !MetadataUtil.TAG_MDMetadata.equals(elm.getName()))
			return null;
        String val;
        String[] vals;
		boolean isPlanned = MetadataUtil.MD_ProgressCode.EXPECTED.equals((String)items.get("idStatus")); // 予定メタデータかどうか
        // ファイル識別子（fileIdentifier）
        val = (String)items.get("mdFileID");
        if(val != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getFileIdentifier(elm))) )
            MetadataUtil.setFileIdentifier(val,elm);
        // 言語（language/isoCode）
        val = (String)items.get("mdLang");
        if(val == null)
            val = "jpn";
        MetadataUtil.setLanguage(val,elm);
        // 文字集合（CharacterSet）
        val = (String)items.get("mdChar");
        if(val == null)
            val = MetadataUtil.MD_CharacterSetCode.UTF_8;
        MetadataUtil.setCharacterSet(val,elm);
        // 階層レベル（hierarchyLevel）
        vals = (String[])items.get("mdHrLv");
        if(vals == null)
            vals = new String[]{MetadataUtil.MD_ScopeCode.DATASET};
        MetadataUtil.setHierarchyLevel(vals,elm);
        // 問い合わせ先（組織名）（contact/organisationName）
        val = (String)items.get("mdContact_rpOrgName");
        if(val != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getOrganisationName(elm))) )
            MetadataUtil.setOrganisationName(val,elm);
        // 問い合わせ先（電話番号）（contact/contactInfo/phone/voice）
        vals = (String[])items.get("mdContact_cntPhone");
        if(vals != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getPhone(elm)) ))
            MetadataUtil.setPhone(vals,elm);
        // 問い合わせ先（住所詳細）（contact/contactInfo/address/deliveryPoint）
        vals = (String[])items.get("mdContact_delPoint");
        if(vals != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getDeliveryPoint(elm)) ))
            MetadataUtil.setDeliveryPoint(vals,elm);
        // 問い合わせ先（市区町村）（contact/contactInfo/address/city）
        val = (String)items.get("mdContact_city");
        if(val != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getCity(elm))) )
            MetadataUtil.setCity(val,elm);
        // 問い合わせ先（都道府県名）（contact/contactInfo/address/administrativeArea）
        val = (String)items.get("mdContact_adminArea");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getAdministrativeArea(elm))) ))
            MetadataUtil.setAdministrativeArea(val,elm);
        // 問い合わせ先（郵便番号）（contact/contactInfo/address/postalCode）
        val = (String)items.get("mdContact_postCode");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getPostalCode(elm))) ))
            MetadataUtil.setPostalCode(val,elm);
        // 問い合わせ先（国）（contact/contactInfo/address/country）
        val = (String)items.get("mdContact_country");
        if(val == null)
            val = "jpn";
        MetadataUtil.setCountry(val,elm);
        // 問い合わせ先（電子メール）（contact/contactInfo/address/electronicMailAddress）
        vals = (String[])items.get("mdContact_eMailAdd");
        if(vals != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getEMailAddress(elm)) ))
            MetadataUtil.setEMailAddress(vals,elm);
        // 問い合わせ先（リンク）（contact/contactInfo/onlineResource/linkage）
        val = (String)items.get("mdContact_linkage");
        if(val != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getLinkage(elm))) )
            MetadataUtil.setLinkage(val,elm);
        // メタデータの日付（dateStamp）
        val = (String)items.get("mdDateSt");
		if(isPlanned)
			val = "2999-12-31";	// 予定メタデータは固定日付
        if(val != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getDateStamp(elm))) )
            MetadataUtil.setDateStamp(val,elm);
        // メタデータの規格の名称（metadataStandardName）
        val = (String)items.get("mdStanName");
        if(val == null)
            val = "JMP";
        MetadataUtil.setMetadataStandardName(val,elm);
        // メタデータの規格のバージョン（metadataStandardVersion）
        val = (String)items.get("mdStanVer");
        if(val == null)
            val = "2.0";
        MetadataUtil.setMetadataStandardVersion(val,elm);
        // タイトル（identificationInfo/MD_DataIdentification/citation/title）
        val = (String)items.get("resTitle");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getTitle(elm))) ))
            MetadataUtil.setTitle(val,elm);
        // 日付-作成日（識別情報）(identificationInfo/MD_DataIdentification/citation/date/date）
        val = (String)items.get("refDateCrt");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getIdentificationDate(MetadataUtil.CI_DateTypeCode.CREATION,elm))) ))
            MetadataUtil.setIdentificationDate(val,MetadataUtil.CI_DateTypeCode.CREATION,elm);
        // 日付-刊行日（識別情報）(identificationInfo/MD_DataIdentification/citation/date/date）
        val = (String)items.get("refDatePub");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getIdentificationDate(MetadataUtil.CI_DateTypeCode.PUBLICATION,elm))) ))
            MetadataUtil.setIdentificationDate(val,MetadataUtil.CI_DateTypeCode.PUBLICATION,elm);
        // 日付-改訂日（識別情報）(identificationInfo/MD_DataIdentification/citation/date/date）
        val = (String)items.get("refDateRev");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getIdentificationDate(MetadataUtil.CI_DateTypeCode.REVISION,elm))) ))
            MetadataUtil.setIdentificationDate(val,MetadataUtil.CI_DateTypeCode.REVISION,elm);
        // 要約（identificationInfo/MD_DataIdentification/abstract）
        val = (String)items.get("idAbs");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getAbstract(elm))) ))
            MetadataUtil.setAbstract(val,elm);
        // 目的（identificationInfo/MD_DataIdentification/purpose）
        val = (String)items.get("idPurp");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getPurpose(elm))) ))
            MetadataUtil.setPurpose(val,elm);
        // 状態（完成:001 または予定：999）（identificationInfo/MD_DataIdentification/status）
        val = (String)items.get("idStatus");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getStatus(elm))) ))
            MetadataUtil.setStatus(val,elm);
        String role = (String)items.get("Ident_role");// （identificationInfo/MD_DataIdentification/pointOfContact/role）
        if(role == null)
            role = MetadataUtil.CI_RoleCode.POINT_OF_CONTACT;
        // 識別情報問い合わせ先（組織名）（identificationInfo/MD_DataIdentification/pointOfContact/organisationName）
        val = (String)items.get("Ident_rpOrgName");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getOrganisationName(role,elm))) ))
            MetadataUtil.setOrganisationName(role,val,elm);
        // 識別情報問い合わせ先（電話番号）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/phone/voice）
        vals = (String[])items.get("Ident_cntPhone");
        if(vals != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getPhone(role,elm)) ))
            MetadataUtil.setPhone(role,vals,elm);
        // 識別情報問い合わせ先（住所詳細）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/deliveryPoint）
        vals = (String[])items.get("Ident_delPoint");
        if(vals != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getDeliveryPoint(role,elm)) ))
            MetadataUtil.setDeliveryPoint(role,vals,elm);
        // 識別情報問い合わせ先（市区町村）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/city）
        val = (String)items.get("Ident_city");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getCity(role,elm))) ))
            MetadataUtil.setCity(role,val,elm);
        // 識別情報問い合わせ先（都道府県名）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/administrativeArea）
        val = (String)items.get("Ident_adminArea");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getAdministrativeArea(role,elm))) ))
            MetadataUtil.setAdministrativeArea(role,val,elm);
        // 識別情報問い合わせ先（郵便番号）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/postalCode）
        val = (String)items.get("Ident_postCode");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getPostalCode(role,elm))) ))
            MetadataUtil.setPostalCode(role,val,elm);
        // 識別情報問い合わせ先（国）（デフォルト）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/country）
        val = (String)items.get("Ident_country");
        if(val == null)
            val = "jpn";
        MetadataUtil.setCountry(role,val,elm);
        // 識別情報問い合わせ先（電子メール）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/electronicMailAddress）
        vals = (String[])items.get("Ident_eMailAdd");
        if(vals != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getEMailAddress(role,elm)) ))
            MetadataUtil.setEMailAddress(role,vals,elm);
        // 識別情報問い合わせ先（リンク）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/onlineResource/linkage）
        val = (String)items.get("Ident_linkage");
        if(val != null && (!replaceIfEmpty || (isEmptyValue(MetadataUtil.getLinkage(role,elm))) ))
            MetadataUtil.setLinkage(role,val,elm);
        // サムネイル（identificationInfo/MD_DataIdentification/graphicOverview/MD_BrowseGraphic/fileName）
        val = (String)items.get("bgFileName");
        if(val != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getBrowseGraphic(elm)) ))
            MetadataUtil.setBrowseGraphic(val,null,null,elm);
        // キーワード（identificationInfo/MD_DataIdentification/descriptiveKeywords/MD_Keywords/keyword,type）
        String kt = (String)items.get("keyTyp");
        if(kt == null)
            kt = MetadataUtil.MD_KeywordTypeCode.THEME;
        vals = (String[])items.get("Keyword");
        if(vals != null)
            MetadataUtil.setKeywords(kt,vals,elm);
        // 利用制限（identificationInfo/MD_DataIdentification/resourceConstraints/MD_Constraints/useLimitation）
        vals = (String[])items.get("useLimit");
        if(vals != null)
            MetadataUtil.setUseLimitation(vals,elm);
        // 識別情報言語（identificationInfo/MD_DataIdentification/language/isoCode）
        val = (String)items.get("dataLang");
        if(val == null)
            val = "jpn";
        MetadataUtil.setDataLanguage(val,elm);
        // 識別情報文字集合（identificationInfo/MD_DataIdentification/characterSet）
        val = (String)items.get("dataChar");
        if(val == null)
            val = MetadataUtil.MD_CharacterSetCode.UTF_8;
        MetadataUtil.setDataCharacterSet(val,elm);
        // 主題分類（identificationInfo/MD_DataIdentification/topicCategory）
        vals = (String[])items.get("tpCat");
        if(vals != null)
            MetadataUtil.setTopicCategory(vals,elm);
        // 地理境界ボックス（identificationInfo/MD_DataIdentification/extent/geographicElement/EX_GeographicBoundingBox/westBoundLongitude,eastBoundLongitude,southBoundLatitude,northBoundLatitude）
        vals = (String[])items.get("HoriBndBox");
        if(vals != null)
            MetadataUtil.setGeographicBoundingBox("WGS84/(B,L)",Double.parseDouble(vals[0]),Double.parseDouble(vals[1]),Double.parseDouble(vals[2]),Double.parseDouble(vals[3]),elm);
        // 時間要素瞬間（identificationInfo/MD_DataIdentification/extent/temporalElement/EX_TemporalExtent/extent/instant）
        val = (String)items.get("instTemp");
        if(val != null && (!replaceIfEmpty || isEmptyValue(MetadataUtil.getTemporalElement(elm)) ))
            MetadataUtil.setTemporalElement(val,elm);
        // 時間要素範囲（identificationInfo/MD_DataIdentification/extent/temporalElement/EX_TemporalExtent/extent/beginEnd/begin,end）
        vals = (String[])items.get("exTemp");
        if(vals != null)
            MetadataUtil.setTemporalElement(vals[0],vals[1],elm);
		// FIXME! 置き換えになっていない
		if(items.get("onlineSrc_WMS") != null || items.get("onlineSrc_WFS") != null){
			String[][] curlist = MetadataUtil.getDataAccessURL(elm);
			ArrayList<String[]> newlist = new ArrayList<String[]>();
			for(int i = 0;curlist != null && i < curlist.length;i++)
				newlist.add(curlist[i]);
			// 配布情報（WMS）（distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/linkage）
			vals = (String[])items.get("onlineSrc_WMS");
			if(vals != null){
				boolean found = false;
				for(String[] dinfo : newlist){
					if(dinfo[4].equals("WMS") || dinfo[4].startsWith("WMS:")){
						dinfo[0] = "WMS";
						dinfo[1] = null;
						dinfo[2] = vals[0];
						dinfo[3] = "http";
						dinfo[4] = "WMS"+(vals.length>1&&vals[1]!=null?":"+vals[1]:"");
						dinfo[5] = null;
						dinfo[6] = null;
						dinfo[7] = null;
						found = true;
						break;
					}
				}
				if(!found)
					newlist.add(new String[]{"WMS",null,vals[0],"http","WMS"+(vals.length>1&&vals[1]!=null?":"+vals[1]:""),null,null,null});
			}
			// 配布情報（WFS）（distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/linkage）
			vals = (String[])items.get("onlineSrc_WFS");
			if(vals != null){
				boolean found = false;
				for(String[] dinfo : newlist){
					if(dinfo[4].equals("WFS") || dinfo[4].startsWith("WFS:")){
						dinfo[0] = "WFS";
						dinfo[1] = null;
						dinfo[2] = vals[0];
						dinfo[3] = "http";
						dinfo[4] = "WFS"+(vals.length>1&&vals[1]!=null?":"+vals[1]:"");
						dinfo[5] = null;
						dinfo[6] = null;
						dinfo[7] = null;
						found = true;
						break;
					}
				}
				if(!found)
					newlist.add(new String[]{"WFS",null,vals[0],"http","WFS"+(vals.length>1&&vals[1]!=null?":"+vals[1]:""),null,null,null});
			}
			// 設定
			MetadataUtil.setDataAccessURL(newlist.toArray(new String[newlist.size()][]),elm);
		}
        return MetadataUtil.ElementToXML(elm,true);
    }

    public static boolean isEmptyValue(Object val) {
    	if(val instanceof String) {
        	return StringUtil.isEmpty((String) val);
    	}
    	else if(val instanceof String[][]) {
        	// １つでも空でない値がないか
    		for(String[] array : (String[][])val) {
    			if(!isEmptyValue(array))
    				return false;
    		}
    	}
    	else if(val instanceof String[]) {
        	// １つでも空でない値がないか
        	for(String str: ((String[]) val)) {
        		if(!isEmptyValue(str)) {
        			return false;
        		}
        	}
    	}
    	else if(val instanceof List) {
        	// １つでも空でない値がないか
        	for(Object listVal : (List<?>)val) {
        		if(!isEmptyValue(listVal)) {
        			return false;
        		}
        	}
    	}
    	else {
    		if(val!=null) return false;
    	}
    	// null is empty
    	return true;
    }


    public static boolean isEmptyValue(List<Element> vals) {
    	if(vals==null) return true;
    	// １つでも空でない値がないか
    	for(Element val : vals) {
    		if(val!=null) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * 文字列を URL エンコードする
     * null は空文字に置換
     *
     * @param s エンコード対象文字列
     * @return URLエンコードされた文字列。
     */
    static String urlencode(String s){
        try{
            s = s!=null?URLEncoder.encode(s,"UTF-8"):"";
        }catch(UnsupportedEncodingException e){
            // ignore
        }
        return s;
    }

    /**
     * Map -> JSON
     *
     * @param items キーと値のマップ
     * @return JSON 文字列
     */
	static String mapToJSON(Map<String,Object> items){
		/*
		JSONObject json = new JSONObject();
		try {
			for(Map.Entry<String,Object> ent : items.entrySet()) {
				String k = ent.getKey();
				Object v = ent.getValue();
				StringEscapeUtils.escapeJavaScript(str);
				json.put(k, v);
			}
			return json.toString();
		} catch(JSONException e) {
			return "{error:\""+e.getMessage()+"\"}";
		}
		*/
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        Iterator<Map.Entry<String,Object>> itr = items.entrySet().iterator();
        for(int i = 0;itr.hasNext();i++){
            Map.Entry<String,Object> ent = itr.next();
            String k = ent.getKey();
            Object v = ent.getValue();
            if(i > 0)
                sb.append(',');
            sb.append('"');
            sb.append(k);
            sb.append('"');
            sb.append(':');
            if(v != null){
                if(v instanceof String[]){
                    String[] arr = (String[])v;
                    sb.append('[');
                    int j = 0;
                    for(String e : arr){
                        if(j > 0)
                            sb.append(',');
                        sb.append('"');
                        sb.append(e);
                        sb.append('"');
                        j++;
                    }
                    sb.append(']');
                }else if(v instanceof Map<?,?>){
                    sb.append(mapToJSON((Map<String,Object>)v));
                }else if(v instanceof Map<?,?>[]){
                    Map<String,Object>[] arr = (Map<String,Object>[])v;
                    sb.append('[');
                    int j = 0;
                    for(Map<String,Object> e : arr){
                        if(j > 0)
                            sb.append(',');
                        sb.append(mapToJSON(e));
                        j++;
                    }
                    sb.append(']');
                }else{
                    sb.append('"');
                    // ダブルクォートがあればエスケープ " -> \"
                    sb.append(v.toString().replaceAll("\"", "\\\\\""));
                    sb.append('"');
                }
            }else{
                sb.append("null");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * デフォルトのクリアリングハウスに HTTP GET/POST リクエストを送信する
     *
     * @param query 送信するクエリ（GET/POST）またはデータ（POST）
     * @param method GETまたはPOSTを指定
     * @param response レスポンスを格納する（出力）
     * @return HTTP Status
     */
    static int sendHttpRequest(String query,String method,StringBuffer response){
    	return sendHttpRequest(CSWUtil.cswurl, CSWUtil.cswuser, CSWUtil.cswpasswd, query, method, response);
    }
    /**
     * HTTP GET/POST リクエストを送信する
     *
     * @param cswurl 送信先クリアリングハウスのURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param query 送信するクエリ（GET/POST）またはデータ（POST）
     * @param method GETまたはPOSTを指定
     * @param response レスポンスを格納する（出力）
     * @return HTTP Status
     */
    static int sendHttpRequest(String cswurl, String user, String passwd, String query,String method,StringBuffer response){
		long starttime = System.currentTimeMillis();
        int httpstatus;
        HttpURLConnection urlcon = null;
        String url = null;
        try{
            url = cswurl;
            if("GET".equals(method))
                url = cswurl+"?"+query;


            logger.info("--- sendHttpRequest[BasicAuth="+StringUtil.isNotEmpty(user)+"], url:" + url);
            URL urlobj = new URL(url);
            urlcon = (HttpURLConnection)urlobj.openConnection();
            urlcon.setUseCaches(false);
            urlcon.setDefaultUseCaches(false);
            urlcon.setInstanceFollowRedirects(true);

            if(StringUtil.isNotEmpty(user)) {
                final String userPassword = user + ":" + passwd;
                String encodeAuthorization = Base64.encodeBase64String(userPassword.getBytes());
                urlcon.setRequestProperty("Authorization", "Basic " + encodeAuthorization);
            }
            urlcon.setRequestMethod(method);
            // String referer = request.getHeader("Referer");
            // if(referer != null)
            //     urlcon.setRequestProperty("Referer",referer);
            // String remotehost = request.getHeader("X-RemoteHost");
            // if(remotehost == null)
            //     remotehost = request.getRemoteHost();
            // if(remotehost != null)
            //     urlcon.setRequestProperty("X-RemoteHost",remotehost);
            // urlcon.setRequestProperty("Cookie","JSESSIONID="+sessionid);
            if("POST".equals(method)){
                urlcon.setDoOutput(true);
                BufferedOutputStream reqbos = new BufferedOutputStream(urlcon.getOutputStream());
                reqbos.write(query.toString().getBytes("UTF-8"));
                reqbos.close();
            }
            httpstatus = urlcon.getResponseCode();
            logger.info("--- sendHttpRequest statuscode: "+httpstatus);
            // // debug
            // for(int i = 0;;i++){
            //     String k = urlcon.getHeaderFieldKey(i);
            //     String v = urlcon.getHeaderField(i);
            //     if(v == null)
            //         break;
            //     logger.debug("sendHttpRequest response header: "+k+"="+v);
            // }
            if(httpstatus == HttpURLConnection.HTTP_OK){
                BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buff = new byte[4096];
                for(;;){
                    int len = bis.read(buff,0,buff.length);
                    if(len == -1)
                        break;
                    bos.write(buff,0,len);
                }
                response.append(bos.toString("UTF-8"));
            }
            return httpstatus;
        }catch(Exception e){
        	logger.error("url: "+url);
            logger.error(e,e);
            return -1;
        }finally{
        	// https の場合、SSLオーバヘッドでリクエスト時間が延びるため、disconnectしてしまうとリクエストのたびにオーバヘッドがかかってしまう。
        	// リクエストが複数連続する場合は KeepAlive を利用してSSLのオーバヘッドを少なくするために disconnect() で接続を切らないようにする。
        	//if(urlcon != null)
        	//urlcon.disconnect();
    		long endtime = System.currentTimeMillis();
    		logger.info("[MethodDuration] CSWUtil.sendHttpRequest elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");
        }
    }

    /**
     * デフォルトのクリアリングハウスに HTTP POST(multipart) リクエストを送信する
     * @param params 送信するパラメータ名と値
     * @param datas 送信するパラメータ名とデータ
     * @param fnames datas のデータのファイル名
     * @param response レスポンスを格納する（出力）
     * @return HTTP Status
     */
    static int sendHttpMultipartRequest(Map<String,String> params,Map<String,byte[]> datas,Map<String,String> fnames,StringBuffer response){
    	return sendHttpMultipartRequest(CSWUtil.cswurl,CSWUtil.cswuser,CSWUtil.cswpasswd, params, datas, fnames, response);
    }
    /**
     * HTTP POST(multipart) リクエストを送信する
     *
     * @param cswurl送信先クリアリングハウスURL
     * @param user ユーザ名
     * @param passwd パスワード
     * @param params 送信するパラメータ名と値
     * @param datas 送信するパラメータ名とデータ
     * @param fnames datas のデータのファイル名
     * @param response レスポンスを格納する（出力）
     * @return HTTP Status
     */
    static int sendHttpMultipartRequest(String cswurl,String user,String passwd,Map<String,String> params,Map<String,byte[]> datas,Map<String,String> fnames,StringBuffer response){
		long starttime = System.currentTimeMillis();
        int httpstatus;
        HttpURLConnection urlcon = null;
        try{
            logger.info("--- sendHttpMultipartRequest[BasicAuth="+StringUtil.isNotEmpty(user)+"], url:" + cswurl);
            URL urlobj = new URL(cswurl);
            urlcon = (HttpURLConnection)urlobj.openConnection();
			urlcon.setRequestMethod("POST");
            urlcon.setDoInput(true);
            urlcon.setDoOutput(true);
            urlcon.setUseCaches(false);
            urlcon.setDefaultUseCaches(false);
            urlcon.setInstanceFollowRedirects(true);
            if(StringUtil.isNotEmpty(user)) {
                final String userPassword = user + ":" + passwd;
                String encodeAuthorization = Base64.encodeBase64String(userPassword.getBytes());
                urlcon.setRequestProperty("Authorization", "Basic " + encodeAuthorization);
            }
            String boundary = "--------------------"+Long.toString(System.currentTimeMillis(), 16);
            urlcon.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

            // multipart で送信
            MultiPartFormOutputStream mpfos = new MultiPartFormOutputStream(urlcon.getOutputStream(), boundary);
            if(params != null){
                Iterator<Map.Entry<String,String>> itr = params.entrySet().iterator();
                for(;itr.hasNext();){
                    Map.Entry<String,String> ent = itr.next();
                    String k = ent.getKey();
                    String v = ent.getValue();
                    mpfos.writeParam(k,v);
                    logger.debug("sendHttpMultipartRequest param "+k+"="+v);
                }
            }
            if(datas != null){
                Iterator<Map.Entry<String,byte[]>> itr = datas.entrySet().iterator();
                for(int i = 0;itr.hasNext();i++){
                    Map.Entry<String,byte[]> ent = itr.next();
                    String k = ent.getKey();
                    byte[] v = ent.getValue();
                    String fn = fnames!=null?fnames.get(k):null;
                    if(fn == null)
                        fn = "file_"+i+".zzz";
                    mpfos.writeData(k,"application/octet-stream",fn,v);
                }
            }
            mpfos.close();

            httpstatus = urlcon.getResponseCode();
            logger.info("--- sendHttpMultipartRequest statuscode: "+httpstatus);
            // // debug
            // for(int i = 0;;i++){
            //     String k = urlcon.getHeaderFieldKey(i);
            //     String v = urlcon.getHeaderField(i);
            //     if(v == null)
            //         break;
            //     logger.debug("sendHttpMultipartRequest response header: "+k+"="+v);
            // }
            if(httpstatus == HttpURLConnection.HTTP_OK){
                BufferedInputStream bis = new BufferedInputStream(urlcon.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buff = new byte[4096];
                for(;;){
                    int len = bis.read(buff,0,buff.length);
                    if(len == -1)
                        break;
                    bos.write(buff,0,len);
                }
                response.append(bos.toString("UTF-8"));
            }
            return httpstatus;
        }catch(Exception e){
            logger.error(e,e);
            return -1;
        }finally{
        	// https の場合、SSLオーバヘッドでリクエスト時間が延びるため、disconnectしてしまうとリクエストのたびにオーバヘッドがかかってしまう。
        	// リクエストが複数連続する場合は KeepAlive を利用してSSLのオーバヘッドを少なくするために disconnect() で接続を切らないようにする。
        	//if(urlcon != null)
        	//urlcon.disconnect();
    		long endtime = System.currentTimeMillis();
    		logger.info("[MethodDuration] CSWUtil.sendHttpMultipartRequest elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");
        }
    }

    /**
     * multipart 出力クラス
     */
    static class MultiPartFormOutputStream {
        // 改行
        private static final String NEWLINE = "\r\n";
        // バウンダリの接頭辞
        private static final String PREFIX = "--";
        // 出力ストリーム
        private DataOutputStream out = null;
        // バウンダリ
        private String boundary = null;

        /**
         * コンストラクタ
         *
         * @param  os        multipart の出力先ストリーム（HTTPURLConnection の outputstream）
         * @param  boundary  multipart のバウンダリ
         */
        public MultiPartFormOutputStream(OutputStream os,String boundary){
            this.out = new DataOutputStream(os);
            this.boundary = boundary;
        }

         /**
         * パラメータの書き込み
         *
         * @param  name パラメータ名
         * @param  value  値
         */
        public void writeParam(String name,String value) throws IOException {
            /*
              --boundary\r\n
              Content-Disposition: form-data; name="<fieldName>"\r\n
              \r\n
              <value>\r\n
            */
            // write boundary
            out.writeBytes(PREFIX);
            out.writeBytes(boundary);
            out.writeBytes(NEWLINE);
            // write content header
            out.writeBytes("Content-Disposition: form-data; name=\""+name+"\"");
            out.writeBytes(NEWLINE);
            out.writeBytes(NEWLINE);
            // write content
            out.writeBytes(value);
            out.writeBytes(NEWLINE);
            out.flush();
        }

        /**
         * データ（form でいうファイル）の書き込み
         *
         * @param  name パラメータ名
         * @param  mimeType  ファイルの MimeType
         * @param  fileName  ファイル名
         * @param  data      データ
         */
        public void writeData(String name,String mimeType,String fileName,byte[] data) throws IOException {
            /*
              --boundary\r\n
              Content-Disposition: form-data; name="<fieldName>"; filename="<filename>"\r\n
              Content-Type: <mime-type>\r\n
              \r\n
              <file-data>\r\n
            */
            // write boundary
            out.writeBytes(PREFIX);
            out.writeBytes(boundary);
            out.writeBytes(NEWLINE);
            // write content header
            out.writeBytes("Content-Disposition: form-data; name=\""+name+"\"; filename=\""+fileName+"\"");
            out.writeBytes(NEWLINE);
            if(mimeType != null){
                out.writeBytes("Content-Type: "+mimeType);
                out.writeBytes(NEWLINE);
            }
            out.writeBytes(NEWLINE);
            // write content
            out.write(data,0,data.length);
            out.writeBytes(NEWLINE);
            out.flush();
        }

        /**
         * multipart の出力を終了しストリームを閉じる
         */
        public void close() throws IOException {
            // write final boundary
            out.writeBytes(PREFIX);
            out.writeBytes(boundary);
            out.writeBytes(PREFIX);
            out.writeBytes(NEWLINE);
            out.flush();
            out.close();
        }
    }


    /**
     * URL(WMSCapabilities)でメタデータ取得
     * クリアリングハウスに GetRecordById リクエストを送信する方式も対応。
     *
     * @param id メタデータのファイル識別子
     * @param schema レスポンスのスキーマ定義
     * @return メタデータ（MD_Metadata XML）
     */
    public static String getWMSCapabilities(String wmsCapsUrl){
		long starttime = System.currentTimeMillis();
    	try{
    		if (wmsCapsUrl != null) wmsCapsUrl = URLDecoder.decode(wmsCapsUrl, "UTF-8");
        }catch(UnsupportedEncodingException e){
            // ignore
        }

    	int httpstatus;
    	StringBuffer sb = new StringBuffer();
    	HttpURLConnection urlcon = null;
    	WMSCapabilities caps = null;
    	int i;

    	LinkedHashMap<String,Object> items = new LinkedHashMap<String,Object>();	//Data for JSON

    	try {
	    	URL urlobj = new URL(wmsCapsUrl);
	        urlcon = (HttpURLConnection)urlobj.openConnection();
	        urlcon.setUseCaches(false);
	        urlcon.setDefaultUseCaches(false);
	        urlcon.setInstanceFollowRedirects(true);
	        httpstatus = urlcon.getResponseCode();
    	}
	    catch (Exception e) {
	    	e.printStackTrace();
			return null;
    	}


    	try {
    		WMSGetCapabilitiesResponse capsResponse = new WMSGetCapabilitiesResponse(new org.geotools.data.ows.SimpleHttpClient.SimpleHTTPResponse(urlcon));
    		caps = (WMSCapabilities)capsResponse.getCapabilities();

    		Service service = caps.getService();

    		String wmsName = service.getName();
    		String wmsTitle = service.getTitle();
    		String wmsAbstract = service.get_abstract();
    		String wmsGetMapUrl = "";
    		String wmsLegendUrl = "";
    		String wmsFeatureUrl = "";
    		String attribution = "";
    		OperationType getMap = caps.getRequest().getGetMap();

    		//WMS URLs
			try {
				getMap = caps.getRequest().getGetMap();
				wmsGetMapUrl = getMap.getGet().toString();
				if (wmsGetMapUrl.endsWith(".xml?")) wmsGetMapUrl = wmsGetMapUrl.substring(0, wmsGetMapUrl.length()-1);
				else if (wmsGetMapUrl.indexOf('?') != wmsGetMapUrl.length()-1) wmsGetMapUrl.substring(0, wmsGetMapUrl.length()-1);
			} catch (Exception e) {}
			try {
				OperationType getLegendGraphic = caps.getRequest().getGetLegendGraphic();
				if (getLegendGraphic != null && getLegendGraphic.getGet() != null) wmsLegendUrl = getLegendGraphic.getGet().toString();
			} catch (Exception e) {}
			try {
				OperationType getFeatureInfo = caps.getRequest().getGetFeatureInfo();
				if (getFeatureInfo != null && getFeatureInfo.getGet() != null) wmsFeatureUrl = getFeatureInfo.getGet().toString();
			} catch (Exception e) {}


    		//WMS概要情報を取得
    		items.put("wmsCapsUrl", wmsCapsUrl);
    		items.put("wmsUrl", wmsGetMapUrl);
    		items.put("wmsLegendUrl", wmsLegendUrl);
    		items.put("wmsFeatureUrl", wmsFeatureUrl);
    		items.put("wmsName", wmsName);
    		items.put("wmsTitle", wmsTitle);
    		items.put("wmsAbstract", StringUtils.escapeHTML(wmsAbstract));
    		items.put("attribution", StringUtils.escapeHTML(attribution));

    		//対応画像形式を取得
    		ArrayList formatList = new ArrayList();
    		if (getMap != null) {
	    		List formats = getMap.getFormats();
	    		for (i=0; i<formats.size(); i++) {
	    			String format = formats.get(i).toString();
	    			if (format.matches("^image\\/(png|png8|gif|jpeg|jpg)")) {
	    				formatList.add(format);
	    			}
	    		}
    		}
    		if (formatList.size() == 0) {
    			formatList.add("image/png");
    			formatList.add("image/gif");
    		}

    			//String[]　へ変換
    		String[] strs = new String[formatList.size()];
    		for (i = 0; i < formatList.size(); i++)
    			strs[i] = (String)formatList.get(i);
    		items.put("formats", strs);

    		//レイヤリストを取得
    		ArrayList<LinkedHashMap<String,Object>> layerList = new ArrayList<LinkedHashMap<String,Object>>();
    		List layers = caps.getLayerList();
			for (i=0; i<layers.size(); i++) {
				Layer layer = (Layer)layers.get(i);

				//1つのレイヤのデータ定義
				int level = 0;
				double minScale = layer.getScaleDenominatorMin();
				double maxScale = layer.getScaleDenominatorMax();
				String layerName = layer.getName();
				Layer[] children = layer.getChildren();
				boolean isGroup = layerName==null||layerName.length()==0||(children!=null&&children.length>0);
				String name = "", title = "", layerId = "";

				if (!isGroup) {
					Layer parent = layer.getParent();
					while (parent!=null) {
						level++;
						parent = parent.getParent();
					}
					if (level > 0){
						name = (isGroup)? "Group" : StringUtils.escapeHTML(layerName);
						title = (layer.getTitle()==null)? "" : StringUtils.escapeHTML(layer.getTitle());
						layerId = layerName;
						if (layerId == null) layerId = "";
					}
				}

				LinkedHashMap<String,Object> layerMap = new LinkedHashMap<String,Object>();
				layerMap.put("isGroup", 	isGroup ? 1 : 0);
				layerMap.put("layerName", 	layerName);
				layerMap.put("layerId", 	layerId);
				layerMap.put("title", 		title);
				layerMap.put("minScale", 	(!Double.isNaN(minScale) ? minScale : 0));
				layerMap.put("maxScale", 	(!Double.isNaN(maxScale) ? maxScale : 0));
				layerMap.put("name", 		name);
				layerMap.put("level", 		level);
				layerList.add(layerMap);
			}

			items.put("layers",(LinkedHashMap<String,Object>[])layerList.toArray(new LinkedHashMap<?,?>[layerList.size()]));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

    	// https の場合、SSLオーバヘッドでリクエスト時間が延びるため、disconnectしてしまうとリクエストのたびにオーバヘッドがかかってしまう。
    	// リクエストが複数連続する場合は KeepAlive を利用してSSLのオーバヘッドを少なくするために disconnect() で接続を切らないようにする。
    	//if(urlcon != null)
    	//urlcon.disconnect();
		long endtime = System.currentTimeMillis();
		logger.info("[MethodDuration] CSWUtil.getWMSCapabilities elapsed: "+String.format("%.2f", (double)(endtime-starttime)/1000)+"s (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");

    	return mapToJSON(items);
    }


    /**
     * デバッグ用メイン
     */
    public static void main(String[] args){
        try{
            if(args.length > 0){
                if("GetRecords".equals(args[0])){
                    HashMap<String,String> condition = new HashMap<String,String>();
                    if(args.length > 1)
                        condition.put("KEYWORD",args[1]);
                    if(args.length > 2)
                    condition.put("CONTACTNAME",args[2]);
                    String xml = getRecords(condition,1,200,1);
                    System.out.println(xml);
                    System.out.println(GetRecordsResponseToJSON(xml));
                }else if("GetRecordById".equals(args[0]) && args.length > 1){
                    String xml = getRecordById(args[1]);
                    System.out.println(xml);
                    System.out.println(MDMetadataToJSON(xml));
                }else if("UploadMetadata".equals(args[0]) && args.length > 1){
                    // java.io.InputStreamReader isr = new java.io.InputStreamReader(new java.io.FileInputStream(args[1]),"UTF-8");
                    // StringBuffer xml = new StringBuffer();
                    // char[] buf = new char[1024];
                    // for(;;){
                    //     int len = isr.read(buf,0,buf.length);
                    //     if(len == -1)
                    //         break;
                    //     xml.append(buf,0,len);
                    // }
                    // isr.close();
                    String xml = constructMetadata(createSampleMetadata());
                    // System.out.println(xml);
                    byte[] tn = null;
                    String fn = null;
                    if(args.length > 2){
                        java.io.File f = new java.io.File(args[2]);
                        // fn = f.getName();
                        java.io.FileInputStream fis = new java.io.FileInputStream(f);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] dat = new byte[4096];
                        for(;;){
                            int len = fis.read(dat,0,dat.length);
                            if(len == -1)
                                break;
                            bos.write(dat,0,len);
                        }
                        fis.close();
                        tn = bos.toByteArray();
                    }
                    if(uploadMetadata(xml.toString(),tn,fn)){
                        System.out.println("upload succeeded");
                    }else{
                        System.out.println("upload failed");
                    }
                }else if("DeleteMetadata".equals(args[0]) && args.length > 1){
                    if(deleteMetadata(args[1]))
                        System.out.println("delete succeeded");
                    else
                        System.out.println("delete failed");
                }else{
                    System.out.println("invalid request: "+args[0]);
                }
            }else{
				Map<String,Object> items = createSampleMetadata();
				String xml = constructMetadata(items);
                System.out.println(xml);
				items = new HashMap<String,Object>();
				items.put("onlineSrc_WMS",new String[]{"http://www.yahoo.co.jp",null});
				xml = replaceMetadata(xml,items);
                System.out.println(xml);
				items.put("onlineSrc_WFS",new String[]{"http://www.google.co.jp",null});
				xml = replaceMetadata(xml,items);
                System.out.println(xml);

				items = createSampleMetadata();
				items.remove("onlineSrc_WMS");
				items.remove("onlineSrc_WFS");
				xml = constructMetadata(items);
                System.out.println(xml);
				items = new HashMap<String,Object>();
				items.put("onlineSrc_WMS",new String[]{"http://www.yahoo.co.jp","lay1,lay2"});
				xml = replaceMetadata(xml,items);
                System.out.println(xml);
				items.put("onlineSrc_WFS",new String[]{"http://www.google.co.jp","lay3,lay4"});
				xml = replaceMetadata(xml,items);
                System.out.println(xml);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String date = sdf.format (new java.util.Date() );
                System.out.println(date);
                java.util.Date d2 = sdf.parse("2013-1-1");
                System.out.println(d2);
                System.out.println("argument required");
            }
        }catch(Exception e){
    		logger.error(e.getMessage(), e);
        }
    }
    static Map<String,Object> createSampleMetadata(){
        HashMap<String,Object> items = new HashMap<String,Object>();
        items.put("mdFileID","SAIGAITASK_20130910_01");//java.util.UUID.randomUUID().toString());
        // items.put("mdLang","jpn");
        // items.put("mdChar",MetadataUtil.MD_CharacterSetCode.UTF_8);
        // items.put("mdHrLv",new String[]{MetadataUtil.MD_ScopeCode.DATASET});
        items.put("mdContact_rpOrgName","防災科学技術研究所");
        items.put("mdContact_cntPhone",new String[]{"029-851-1611"});
        items.put("mdContact_delPoint",new String[]{"天王台3-1"});
        items.put("mdContact_city","つくば市");
        items.put("mdContact_adminArea","茨城県");
        items.put("mdContact_postCode","305-0006");
        // items.put("mdContact_country","jpn");
        items.put("mdContact_eMailAdd",new String[]{"toiawase@bosai.go.jp"});
        items.put("mdContact_linkage","http://www.bosai.go.jp/");
        items.put("mdDateSt",new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        // items.put("mdStanName","JMP");
        // items.put("mdStanVer","2.0");
        items.put("resTitle","サンプルメタデータ");
        items.put("refDateCrt","2010-10-25");
        items.put("refDatePub","2011-7-10");
        items.put("refDateRev",new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        items.put("idAbs","メタデータのサンプルで内容に意味はありません");
        items.put("idPurp","サンプルを提示するために作成されました");
        items.put("idStatus",MetadataUtil.MD_ProgressCode.COMPLETED);
        items.put("Ident_role",MetadataUtil.CI_RoleCode.POINT_OF_CONTACT);
        items.put("Ident_rpOrgName","防災科学技術研究所");
        items.put("Ident_cntPhone",new String[]{"029-851-1611"});
        items.put("Ident_delPoint",new String[]{"天王台3-1"});
        items.put("Ident_city","つくば市");
        items.put("Ident_adminArea","茨城県");
        items.put("Ident_postCode","305-0006");
        // items.put("Ident_country","jpn");
        items.put("Ident_eMailAdd",new String[]{"toiawase@bosai.go.jp"});
        items.put("Ident_linkage","http://www.bosai.go.jp/");
        items.put("bgFileName","http://hazardmap.service-section.com/gserver/preview/kiban_map_10m.jpeg");
        items.put("Keyword",new String[]{"サンプル","sample","例"});
        items.put("useLimit",new String[]{"002","サンプルのため使用不可"});
        // items.put("dataLang","jpn");
        // items.put("dataChar",MetadataUtil.MD_CharacterSetCode.UTF_8);
        items.put("tpCat",new String[]{MetadataUtil.MD_TopicCategoryCode.SOCIETY});
        items.put("HoriBndBox",new String[]{"10","20","30","40"});
        items.put("instTemp",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new java.util.Date()));
        // items.put("exTemp",new  String[]{"2013-1-1T00:00:00","2013-12-31T23:59:59"});
        items.put("onlineSrc_WMS",new String[]{"http://hazardmap.service-section.com/geoserver/wmscapabilities?&id=kiban_map_10m","layer1,layer2"});
        items.put("onlineSrc_WFS",new String[]{"http://hazardmap.service-section.com/geoserver/wfscapabilities?&id=kiban_map_10m","layer10,layer20"});
        return items;
    }
}
