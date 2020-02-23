/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.publiccommons;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;

import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.dto.GeneralInformationDto;
import jp.ecom_plat.saigaitask.dto.PCommonsSendDto;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.PublicCommonsUtils;
import jp.ne.publiccommons.xml.pcxml1._1_3.addition.TypeFile;
import jp.ne.publiccommons.xml.pcxml1._1_3.addition.TypeFiles;
import jp.ne.publiccommons.xml.pcxml1._1_3.addition.TypeMediaType;
import jp.ne.publiccommons.xml.pcxml1._1_3.addition.TypeMimeType;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.generalinformation.Description;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.generalinformation.EnumTypeCategory;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.generalinformation.EnumTypeSubCategory;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.generalinformation.TypeDisasterInformationType;
import jp.ne.publiccommons.xml.pcxml1._1_3.body.generalinformation.TypeGeneralInformation;
import jp.ne.publiccommons.xml.pcxml1._1_3.elementbasis.TypeDisaster;
import jp.ne.publiccommons.xml.pcxml1._1_3.html.TypeA;
import jp.ne.publiccommons.xml.pcxml1._1_3.html.TypeBr;
import jp.ne.publiccommons.xml.pcxml1._1_3.html.TypeDl;
import jp.ne.publiccommons.xml.pcxml1._1_3.html.TypeFlow;
import jp.ne.publiccommons.xml.pcxml1._1_3.html.TypeOl;
import jp.ne.publiccommons.xml.pcxml1._1_3.html.TypeUl;
import jp.ne.publiccommons.xml.xml.edxl.CommonsContentObjectType;
import oasis.names.tc.emergency.edxl.de._1.EDXLDistributionType;


/**
 * 公共情報コモンズのおしらせ情報サービス
 */
@org.springframework.stereotype.Service
public class PcGeneralService extends AbstractPublicCommonsService {

	/** PCX_GIファクトリ. おしらせ情報 */
	public jp.ne.publiccommons.xml.pcxml1._1_3.body.generalinformation.ObjectFactory pcxGiFactory =	new jp.ne.publiccommons.xml.pcxml1._1_3.body.generalinformation.ObjectFactory();

	public jp.ne.publiccommons.xml.pcxml1._1_3.html.ObjectFactory pcxhtmlFactory =	new jp.ne.publiccommons.xml.pcxml1._1_3.html.ObjectFactory();


	/** 記録データサービス */
	@Resource protected TrackDataService trackDataService;

	/**
	 * お知らせ情報を生成します.
	 * @param generalInformationList お知らせ情報リスト
	 * @return TypeGeneralInformation
	 */
	public TypeGeneralInformation createGeneralInformation(GeneralInformationDto generalInformationDto) {
		TypeGeneralInformation typeGeneralInformation= pcxGiFactory.createTypeGeneralInformation();

		switch (Integer.parseInt(generalInformationDto.division)) {
		case 1:
			typeGeneralInformation.setCategory(EnumTypeCategory.交通);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.鉄道);
			break;
		case 2:
			typeGeneralInformation.setCategory(EnumTypeCategory.交通);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.バス);
			break;
		case 3:
			typeGeneralInformation.setCategory(EnumTypeCategory.交通);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.航空);
			break;
		case 4:
			typeGeneralInformation.setCategory(EnumTypeCategory.交通);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.船舶);
			break;
		case 5:
			typeGeneralInformation.setCategory(EnumTypeCategory.交通);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.道路);
			break;
		case 6:
			typeGeneralInformation.setCategory(EnumTypeCategory.交通);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.その他);
			break;
		case 7:
			typeGeneralInformation.setCategory(EnumTypeCategory.ライフライン);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.電気);
			break;
		case 8:
			typeGeneralInformation.setCategory(EnumTypeCategory.ライフライン);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.ガス);
			break;
		case 9:
			typeGeneralInformation.setCategory(EnumTypeCategory.ライフライン);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.水道);
			break;
		case 10:
			typeGeneralInformation.setCategory(EnumTypeCategory.ライフライン);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.給水);
			break;
		case 11:
			typeGeneralInformation.setCategory(EnumTypeCategory.ライフライン);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.通信);
			break;
		case 12:
			typeGeneralInformation.setCategory(EnumTypeCategory.ライフライン);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.放送);
			break;
		case 13:
			typeGeneralInformation.setCategory(EnumTypeCategory.ライフライン);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.その他);
			break;
		case 14:
			typeGeneralInformation.setCategory(EnumTypeCategory.生活情報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.行政手続き);
			break;
		case 15:
			typeGeneralInformation.setCategory(EnumTypeCategory.生活情報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.被災者支援);
			break;
		case 16:
			typeGeneralInformation.setCategory(EnumTypeCategory.生活情報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.福祉_教育_保育);
			break;
		case 17:
			typeGeneralInformation.setCategory(EnumTypeCategory.生活情報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.環境);
			break;
		case 18:
			typeGeneralInformation.setCategory(EnumTypeCategory.生活情報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.防犯);
			break;
		case 19:
			typeGeneralInformation.setCategory(EnumTypeCategory.生活情報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.保健衛生);
			break;
		case 20:
			typeGeneralInformation.setCategory(EnumTypeCategory.生活情報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.医療);
			break;
		case 21:
			typeGeneralInformation.setCategory(EnumTypeCategory.生活情報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.その他);
			break;
		case 22:
			typeGeneralInformation.setCategory(EnumTypeCategory.広報);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.広報);
			break;
		case 23:
			typeGeneralInformation.setCategory(EnumTypeCategory.観光_文化);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.観光_文化);
			break;
		case 24:
			typeGeneralInformation.setCategory(EnumTypeCategory.観光_文化);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.その他);
			break;
		case 25:
			typeGeneralInformation.setCategory(EnumTypeCategory.その他);
			typeGeneralInformation.setSubCategory(EnumTypeSubCategory.その他);
			break;

		default:
			break;
		}

		typeGeneralInformation.setTitle(generalInformationDto.title);

		// 本文
		Description description = new Description();
		String text = generalInformationDto.text;

		// コモンズ対応タグのみ変換
		text = text.replaceAll("<h2>", "{@@h2@@}");
		text = text.replaceAll("</h2>", "{@@/h2@@}");
		text = text.replaceAll("<br>", "{@@br@@}");
		text = text.replaceAll("<br/>", "{@@br@@}");
		text = text.replaceAll("<br />", "{@@br@@}");
		text = text.replaceAll("<ul>", "{@@ul@@}");
		text = text.replaceAll("</ul>", "{@@/ul@@}");
		text = text.replaceAll("<ol>", "{@@ol@@}");
		text = text.replaceAll("</ol>", "{@@/ol@@}");
		text = text.replaceAll("<dl>", "{@@dl@@}");
		text = text.replaceAll("</dl>", "{@@/dl@@}");
		text = text.replaceAll("<dt>", "{@@dt@@}");
		text = text.replaceAll("</dt>", "{@@/dt@@}");
		text = text.replaceAll("<dd>", "{@@dd@@}");
		text = text.replaceAll("</dd>", "{@@/dd@@}");
		text = text.replaceAll("<li>", "{@@li@@}");
		text = text.replaceAll("</li>", "{@@/li@@}");
		text = text.replaceAll("<a href=", "{@@a href=@@}");
		text = text.replaceAll("</a>", "{@@/a@@}");

		// 除去
		text = text.replaceAll("<.+?>", "");	// コモンズ非対応タグ
		text =text.replaceAll("\r\n", "");			// 改行コード

		// コモンズ対応タグを戻す
		text = text.replaceAll("\\{@@h2@@\\}", "<h2>");
		text = text.replaceAll("\\{@@/h2@@\\}", "</h2>");
		text = text.replaceAll("\\{@@br@@\\}", "<br> </br>");	// getTypeBrJAXBElement()側の都合で終了タグを付与
		text = text.replaceAll("\\{@@ul@@\\}", "<ul>");
		text = text.replaceAll("\\{@@/ul@@\\}", "</ul>");
		text = text.replaceAll("\\{@@ol@@\\}", "<ol>");
		text = text.replaceAll("\\{@@/ol@@\\}", "</ol>");
		text = text.replaceAll("\\{@@dl@@\\}", "<dl>");
		text = text.replaceAll("\\{@@/dl@@\\}", "</dl>");
		text = text.replaceAll("\\{@@dt@@\\}", "<dt>");
		text = text.replaceAll("\\{@@/dt@@\\}", "</dt>");
		text = text.replaceAll("\\{@@dd@@\\}", "<dd>");
		text = text.replaceAll("\\{@@/dd@@\\}", "</dd>");
		text = text.replaceAll("\\{@@li@@\\}", "<li>");
		text = text.replaceAll("\\{@@/li@@\\}", "</li>");
		text = text.replaceAll("\\{@@a href=@@\\}", "<a href=");
		text = text.replaceAll("\\{@@/a@@\\}", "</a>");

		// タグ毎に分解してJAXBElement生成
		List <String> tagList = getTagList(text);
	    for (String tag : tagList){
			if (tag.startsWith("<br>")) {
				description.getContent().add(getTypeBrJAXBElement());
			} else if (tag.startsWith("<ul>")) {
				description.getContent().add(getTypeUlJAXBElement(tag));
			} else if (tag.startsWith("<ol>")) {
				description.getContent().add(getTypeOlJAXBElement(tag));
			} else if (tag.startsWith("<dl>")) {
				description.getContent().add(getTypeDlJAXBElement(tag));
			}else if (tag.startsWith("<h2>")) {
				description.getContent().add(getStringJAXBElement(tag));
			}else if (tag.startsWith("<a href=")) {
	        	String anchor = tagReplace(tag);
	        	String href = run(tag, "\"");
	        	description.getContent().add(getTypeAhrefJAXBElement(anchor , href));
			} else {
				// プレーンテキストはそのまま出力
				description.getContent().add(tag);
			}
	    }
		typeGeneralInformation.setDescription(description);

		if (!"".equals(generalInformationDto.notificationUri)) {
			typeGeneralInformation.setURL(generalInformationDto.notificationUri);
		}

		if (!"".equals(generalInformationDto.fileUri)) {
			TypeFile typeFile = new TypeFile();
			typeFile.setURI(generalInformationDto.fileUri);

			//MimeTypeのXML設定 (コモンズ側で対応するMimeTypeがない場合は何もセットしない)
			if ("audio/mpeg".equals(generalInformationDto.mimeType)){
				typeFile.setMimeType(TypeMimeType.AUDIO_MPEG);
			} else if("image/jpeg".equals(generalInformationDto.mimeType)){
				typeFile.setMimeType(TypeMimeType.IMAGE_JPEG);
			} else if("text/xml".equals(generalInformationDto.mimeType)){
				typeFile.setMimeType(TypeMimeType.TEXT_XML);
			}  else if("application/pdf".equals(generalInformationDto.mimeType)){
				typeFile.setMimeType(TypeMimeType.APPLICATION_PDF);
			}

			//MediaTypeの設定
			if("Audio".equals(generalInformationDto.mediaType)){
				typeFile.setMediaType(TypeMediaType.AUDIO);
			} else if("Graphic".equals(generalInformationDto.mediaType)){
				typeFile.setMediaType(TypeMediaType.GRAPHIC);
			} else if("Pdf".equals(generalInformationDto.mediaType)){
				typeFile.setMediaType(TypeMediaType.PDF);
			} else if("Photo".equals(generalInformationDto.mediaType)){
				typeFile.setMediaType(TypeMediaType.PHOTO);
			} else if("Text".equals(generalInformationDto.mediaType)){
				typeFile.setMediaType(TypeMediaType.TEXT);
			} else if("Video".equals(generalInformationDto.mediaType)){
				typeFile.setMediaType(TypeMediaType.VIDEO);
			}

			//ファイルタイトルの設定
			if(!"".equals(generalInformationDto.fileCaption) && !"".equals(generalInformationDto.fileUri)){
				typeFile.setCaption(generalInformationDto.fileCaption);
			}

			TypeFiles typeFiles = new TypeFiles();
			typeFiles.getFile().add(typeFile);

//			TypeInformation typeInformation;
//				typeInformation.setFileSize(value);
//				typeInformation.setWidth(value);
//				typeInformation.setHeight(value);
//			typeFile.setInformation(typeInformation);

			typeGeneralInformation.setAttachmentFiles(typeFiles);
		}
		return typeGeneralInformation;

	}

	/**
	 * 指定文字列の3～4文字タグか判定する
	 * @param tag HTMLタグ判定用文字列
	 * @return 文字列がタグの場合はtrue、タグではない場合はfalse
	 */
	public boolean checkTag(String tag) {
		if (tag.equals("<h2>")) return true;
		if (tag.equals("<br>")) return true;
		if (tag.substring(0,3).equals("<a>")) return true;
		if (tag.equals("<ul>")) return true;
		if (tag.equals("<ol>")) return true;
		if (tag.equals("<dl>")) return true;
		if (tag.equals("<dt>")) return true;
		if (tag.equals("<dd>")) return true;
		return false;
	}

	/**
	 * EDXLDistributionType を生成
	 * @param publiccommonsReportData 公共情報コモンズ発信データ
	 * @param generalInformationDto お知らせ情報
	 * @param pCommonsSendDto コモンズ送信情報
	 * @return EDXLDistributionType
	 */
	public EDXLDistributionType createEDXLDistributionType(PubliccommonsReportData publiccommonsReportData, GeneralInformationDto generalInformationDto, PCommonsSendDto pCommonsSendDto) {
		// EDXLDistribution を作成
		EDXLDistributionType edxlDistributionType = createEDXLDistribution(publiccommonsReportData.localgovinfoid, pCommonsSendDto);

		// 公共情報コモンズContentObject を作成
		CommonsContentObjectType commonsContentObjectType = createCommonsContentObjectType(edxlDistributionType, publiccommonsReportData, pCommonsSendDto);
		PublicCommonsUtils.setCommonsContentObject(edxlDistributionType, commonsContentObjectType);

		// おしらせ情報を作成
		TypeGeneralInformation generalInformation = createGeneralInformation(generalInformationDto);

		// 情報識別区分
		if("ACCIDENT".equals(generalInformationDto.disasterInformationType)){
			generalInformation.setDisasterInformationType(TypeDisasterInformationType.ACCIDENT);
		} else if ("ALERT".equals(generalInformationDto.disasterInformationType)){
			generalInformation.setDisasterInformationType(TypeDisasterInformationType.ALERT);
		} else if ("DISASTER".equals(generalInformationDto.disasterInformationType)){
			generalInformation.setDisasterInformationType(TypeDisasterInformationType.DISASTER);

			// 災害名称
			if (StringUtil.isNotEmpty(pCommonsSendDto.trackdataname)) {
				TypeDisaster typeDisaster = pcxEbFactory.createTypeDisaster();
				typeDisaster.setDisasterName(pCommonsSendDto.trackdataname);
				generalInformation.setDisaster(typeDisaster);
			}
		} else if ("ORDINARY".equals(generalInformationDto.disasterInformationType)){
			generalInformation.setDisasterInformationType(TypeDisasterInformationType.ORDINARY);
		} else if ("UNKNOWN".equals(generalInformationDto.disasterInformationType)){
			generalInformation.setDisasterInformationType(TypeDisasterInformationType.UNKNOWN);
		}

		JAXBElement<?> pcxmlJAXBElement = pcxGiFactory.createGeneralInformation(generalInformation);
		PublicCommonsUtils.setPCXML(edxlDistributionType, pcxmlJAXBElement);

		// 作成日時
		if(publiccommonsReportData.createtime != null) {
			PublicCommonsUtils.setCreateDateTime(edxlDistributionType, new Date(publiccommonsReportData.createtime.getTime()));
		}

		// タイトル
		String title = createReportHeadTitle(edxlDistributionType, pCommonsSendDto);
		PublicCommonsUtils.setTitle(edxlDistributionType, title);

		return edxlDistributionType;
	}

	/**
	 * StringJAXBElement を生成
	 * @param text 本文
	 * @return JAXBElement<String>
	 */
	public JAXBElement<String> getStringJAXBElement(String text) {
		JAXBElement<String> StringContent = pcxhtmlFactory.createH2(tagReplace(text));
		return StringContent;
	}

	/**
	 * TypeBrJAXBElement を生成
	 * @return JAXBElement<TypeBr>
	 */
	public JAXBElement<TypeBr> getTypeBrJAXBElement() {
		TypeBr typeBr = new TypeBr();
		JAXBElement<TypeBr> typeBrContent = pcxhtmlFactory.createBr(typeBr);
		return typeBrContent;
	}

	/**
	 * TypeAJAXBElement を生成
	 * @param tagA TypeAタグ内要素
	 * @return JAXBElement<TypeA>
	 */
	public JAXBElement<TypeA> getTypeAhrefJAXBElement(String tagA , String href) {
		TypeA typeA = new TypeA();
		typeA.setContent(tagReplace(tagA));
		typeA.setHref(tagReplace(href));
		JAXBElement<TypeA> typeAContent = pcxhtmlFactory.createA(typeA);
		return typeAContent;
	}

	/**
	 * TypeUlJAXBElement を生成
	 * @param tagUl TypeUlタグ内要素
	 * @return JAXBElement<TypeUl>
	 */
	public JAXBElement<TypeUl> getTypeUlJAXBElement(String tagUl) {
		TypeUl typeUl = new TypeUl();
		tagUl = tagUl.replaceAll("<ul>", "").replaceAll("</ul>", "").replaceAll("<br>", "").replaceAll("</br>", "");

		List<String> tagList = getTagListLi(tagUl);
		int k = 0;
		for (int j = 0 ; j < tagList.size() ; j++){
	        String tagListText = tagList.get(j);
	        if(!" ".equals(tagListText) && !"".equals(tagListText) && tagListText !=null){

	        	//アンカータグがあれば取得
    	        String a = run(tagListText , "<a>");

	    		String li = run(tagListText , "<li>");
	    		li = li.replaceAll(a, "");

	    		TypeFlow typeFlow = new TypeFlow();
    	        if(!"".equals(a)){
    	        	String anchor = tagReplace(a);
    	        	String href = run(a , "\"");
    	        	typeFlow.getContent().add(getTypeAhrefJAXBElement(anchor , href));
    	        }
	    		if(!"".equals(li)){
	    			typeFlow.getContent().add(tagReplace(li));
	    		}
	    		if(!"".equals(li) || !"".equals(a)){
	    			typeUl.getLi().add(k, typeFlow);
	    			k++;
	    		}
	        }
		}
		JAXBElement<TypeUl> typeUlContent = pcxhtmlFactory.createUl(typeUl);
		return typeUlContent;
	}

	/**
	 * TypeOlJAXBElement を生成
	 * @param tagOl TypeOlタグ内要素
	 * @return JAXBElement<TypeOl>
	 */
	public JAXBElement<TypeOl> getTypeOlJAXBElement(String tagOl) {
		TypeOl typeOl = new TypeOl();

		tagOl = tagOl.replaceAll("<ol>", "").replaceAll("</ol>", "").replaceAll("<br>", "").replaceAll("</br>", "");

		List<String> tagList = getTagListLi(tagOl);
		int k = 0;
		for (int j = 0 ; j < tagList.size() ; j++){
	        String tagListText = tagList.get(j);
	        if(!" ".equals(tagListText) && !"".equals(tagListText) && tagListText !=null){

	        	//アンカータグがあれば取得
    	        String a = run(tagListText , "<a>");

	    		String li = run(tagListText , "<li>");
	    		li = li.replaceAll(a, "");

	    		TypeFlow typeFlow = new TypeFlow();
    	        if(!"".equals(a)){
    	        	String anchor = tagReplace(a);
    	        	String href = run(a , "\"");
    	        	typeFlow.getContent().add(getTypeAhrefJAXBElement(anchor , href));
    	        }
	    		if(!"".equals(li)){
	    			typeFlow.getContent().add(tagReplace(li));
	    		}
	    		if(!"".equals(li) || !"".equals(a)){
	    			typeOl.getLi().add(k, typeFlow);
	    			k++;
	    		}
	        }
		}
		JAXBElement<TypeOl> typeOlContent = pcxhtmlFactory.createOl(typeOl);
		return typeOlContent;
	}

	/**
	 * TypeDlJAXBElement を生成
	 * @param tagDl TypeDlタグ内要素
	 * @return JAXBElement<TypeDl>
	 */
	public JAXBElement<TypeDl> getTypeDlJAXBElement(String tagDl) {
		TypeDl typeDl = new TypeDl();

		tagDl = tagDl.replaceAll("<dl>", "").replaceAll("</dl>", "").replaceAll("<br>", "").replaceAll("</br>", "");

		List<String> tagList = getTagListDtOrDd(tagDl);
		for (int j = 0 ; j < tagList.size() ; j++){
	        String tagListText = tagList.get(j);
	        if(!" ".equals(tagListText) && !"".equals(tagListText) && tagListText !=null){
	        	//アンカータグがあれば取得
    	        String a = run(tagListText , "<a>");

				String dt = run(tagListText , "<dt>");
				dt = dt.replaceAll(a, "");
				dt = dt.replaceAll(" ", "").replaceAll("<dt>", "").replaceAll("</dt>", "");
				String dd = run(tagListText , "<dd>");
				dd = dd.replaceAll(a, "");
				dd = dd.replaceAll(" ", "").replaceAll("<dd>", "").replaceAll("</dd>", "");
	    		TypeFlow typeFlow = new TypeFlow();
   	        	String anchor = "";
	        	String href = "";

    	        if(!"".equals(a)){
    	        	anchor = tagReplace(a);
    	        	href = run(a , "\"");
    	        }
				if(!"".equals(dt)){
					typeFlow.getContent().add(tagReplace(dt));
					if(!"".equals(a)){
						typeFlow.getContent().add(getTypeAhrefJAXBElement(anchor, href));
					}
					JAXBElement<TypeFlow> typeDt = pcxhtmlFactory.createDt(typeFlow);
					typeDl.getDtOrDd().add(typeDt);
				}else if(!"".equals(dd)){
					typeFlow.getContent().add((dd));
					if(!"".equals(a)){
						typeFlow.getContent().add(getTypeAhrefJAXBElement(anchor , href));
					}
					JAXBElement<TypeFlow> typeDd = pcxhtmlFactory.createDd(typeFlow);
					typeDl.getDtOrDd().add(typeDd);
				}
	        }
		}
		JAXBElement<TypeDl> typeDlContent = pcxhtmlFactory.createDl(typeDl);
		return typeDlContent;
	}

	/**
	 * タグの中身を取得
	 * @param text 本文
	 * @param tag 中身を取得したいタグ
	 * @return タグの中身
	 */
	public String run(String text , String tag) {
		if (tag.substring(0,1).equals("\"")){
			tag = tag.substring(0,1);

			Pattern elementPattern = Pattern.compile(tag +"(.+?)" + tag, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher matcher = elementPattern.matcher(text);
			if(matcher.find()){
				String content = matcher.group();
				return content.replace("\"", "");
			}else{
				return "";
			}
		} else if (tag.substring(0,3).equals("<a>")){
			tag = tag.substring(0,3);
		}
		tag = tag.replaceAll("<", "").replaceAll(">", "");

		Pattern elementPattern = Pattern.compile(	"<" + tag + ".*?>(.+?)</" + tag + ".*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = elementPattern.matcher(text);
		if(matcher.find()){
			String content = matcher.group();
			return content;
		}else{
			return "";
		}
	}

	/**
	 * タグを除去する
	 * @param text
	 * @return textからタグを除去した文字列
	 */
	public String tagReplace(String text) {
		Pattern pattern = Pattern.compile("<.+?>", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		String content =  matcher.replaceAll("");
		return content;
	}

	/**
	 * タグ毎に分割してリストを返す
	 * @param text
	 * @return
	 */
	public List <String> getTagList(String text) {
		List <String> tagTextList = new ArrayList<String>();

		StringBuffer abc = new StringBuffer();
		String inTag = "";
		String tag = "";

		for ( int i =4; i<=text.length(); i++){
			tag = text.substring(i-4, i);
			if (checkTag(tag)){
				if(!"".equals(abc.toString())){
					tagTextList.add(abc.toString());
					abc = new StringBuffer();
				}
				//			指定したタグの中身を取得する
				inTag = run(text.substring(i-4) , tag);
				if(!"".equals(inTag)){
					tagTextList.add(inTag);
					i = i + inTag.length()-1;
				}
			}else{
				abc.append(tag.substring( 0, 1 ));
			}
		}
		if(!"".equals(abc.toString())){
			tagTextList.add(abc.toString()+tag.substring( 1, 4 ));
		}
		return tagTextList;
	}

	/**
	 * H2をタグ毎に分割してリストを返す
	 * @param text
	 * @return
	 */
	public List <String> getTagListText(String text) {
		List <String> tagTextList = new ArrayList<String>();

		StringBuffer abc = new StringBuffer();
		String inTag = "";
		String tag = "";

		for ( int i =5; i<=text.length(); i++){
			tag = text.substring(i-5, i);
			if (tag.equals("<div>")){
				if(!"".equals(abc.toString())){
					tagTextList.add(abc.toString());
					abc = new StringBuffer();
				}
				//			指定したタグの中身を取得する
				inTag = run(text.substring(i-5) , tag);
				tagTextList.add(inTag);
				i = i + inTag.length();
			}else{
				abc.append(tag.substring( 0, 1 ));
			}
		}
		if(!"".equals(abc.toString())){
			tagTextList.add(abc.toString()+tag.substring( 1, 5 ));
		}
		return tagTextList;
	}

	/**
	 * ul ol をタグ毎に分割してリストを返す
	 * @param text
	 * @return
	 */
	public List <String> getTagListLi(String text) {
		List <String> tagTextList = new ArrayList<String>();

		StringBuffer abc = new StringBuffer();
		String inTag = "";
		String tag = "";

		for ( int i =4; i<=text.length(); i++){
			tag = text.substring(i-4, i);
			if (tag.equals("<li>")){
				if(!"".equals(abc.toString())){
					tagTextList.add(abc.toString());
					abc = new StringBuffer();
				}
				//			指定したタグの中身を取得する
				inTag = run(text.substring(i-4) , tag);
				if(!"".equals(inTag)){
					tagTextList.add(inTag);
					i = i + inTag.length()-1;
				}
			}else{
				abc.append(tag.substring( 0, 1 ));
			}
		}
		if(!"".equals(abc.toString())){
			tagTextList.add(abc.toString()+tag.substring( 1, 4 ));
		}
		return tagTextList;
	}

	/**
	 * ul ol をタグ毎に分割してリストを返す
	 * @param text
	 * @return
	 */
	public List <String> getTagListDtOrDd(String text) {
		List <String> tagTextList = new ArrayList<String>();

		StringBuffer abc = new StringBuffer();
		String inTag = "";
		String tag = "";

		for ( int i =4; i<=text.length(); i++){
			tag = text.substring(i-4, i);
			if (tag.equals("<dd>") || tag.equals("<dt>")){
				if(!"".equals(abc.toString())){
					tagTextList.add(abc.toString());
					abc = new StringBuffer();
				}
				//			指定したタグの中身を取得する
				inTag = run(text.substring(i-4) , tag);
				if(!"".equals(inTag)){
					tagTextList.add(inTag);
					i = i + inTag.length()-1;
				}
			}else{
				abc.append(tag.substring( 0, 1 ));
			}
		}
		if(!"".equals(abc.toString())){
			tagTextList.add(abc.toString()+tag.substring( 1, 4 ));
		}
		return tagTextList;
	}
}
