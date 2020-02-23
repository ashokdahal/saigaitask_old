/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.seasar.framework.util.ResourceUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.NoticetypeMaster;
import jp.ecom_plat.saigaitask.form.page.MailhistoryForm;
import jp.ecom_plat.saigaitask.service.FilteredFeatureService;
import jp.ecom_plat.saigaitask.service.db.FacebookpostdefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.MailhistoryService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.NoticeTemplateService;
import jp.ecom_plat.saigaitask.service.db.NoticedefaultgroupInfoService;


/**
 *
 * 通知履歴を表示するアクションクラスです.
 *
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class MailhistoryAction extends PageBaseAction {

	protected MailhistoryForm mailhistoryForm;
	@Resource
	protected NoticeTemplateService noticeTemplateService;
	@Resource
	protected NoticedefaultgroupInfoService noticedefaultgroupInfoService;
	@Resource
	protected FacebookpostdefaultInfoService facebookpostdefaultInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected FilteredFeatureService filteredFeatureService;
	@Resource
	protected MailhistoryService mailhistoryService;

	/** 通知データリスト */
	public List<NoticemailData> noticemailDataItems;
	/** 通知種別マスタリスト */
	public List<NoticetypeMaster> noticetypeMasterItems;
	/** ページリンク */
	public List<HashMap<String,String>> links = new ArrayList<HashMap<String,String>>();
	/** ページ数 */
	public int pageNum;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("noticemailDataItems", noticemailDataItems);
		model.put("noticetypeMasterItems", noticetypeMasterItems);
		model.put("links", links);
		model.put("pageNum", pageNum);
		model.put("mailhistoryForm", mailhistoryForm);
	}

	/**
	 *	通知履歴ページを表示する.
	 * @return index.jsp
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/mailhistory", "/page/mailhistory/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute MailhistoryForm mailhistoryForm, BindingResult bindingResult) throws ParseException {
		this.mailhistoryForm = mailhistoryForm;
		initPage("mailhistory", mailhistoryForm);

		// 通知種別を取得
		String noticetypeidstr = request.getParameter("noticetypeid");
		int noticetypeid = 1;	// デフォルトは1(メール)
		if (noticetypeidstr != null && !("".equals(noticetypeidstr))) {
			noticetypeid = Integer.parseInt(noticetypeidstr);
		} else {
			noticetypeidstr = "1";		// 初期値は1
		}
		mailhistoryForm.noticetypeid = noticetypeidstr;

		// 現在ページ番号
		mailhistoryForm.numPage = request.getParameter("pageno");
		if(mailhistoryForm.numPage == null){
			mailhistoryForm.numPage = "1";	// 初期値は1
		}

		// 1ページの最大表示件数
		int pageDataCnt = Integer.parseInt(ResourceUtil.getProperties("SaigaiTask.properties").getProperty("PAGE"));
		if (pageDataCnt == 0) pageDataCnt = 20;	// 未指定時は20件

		noticetypeMasterItems = noticetypeMasterService.findAll();
		noticemailDataItems = mailhistoryService.findByNoticetypeId(loginDataDto.getTrackdataid(), noticetypeid, mailhistoryForm.numPage, pageDataCnt);

		for (int i=0; i<noticemailDataItems.size(); i++) {
			// 送信先メールアドレスのカンマを改行に置換
			noticemailDataItems.get(i).mailto = noticemailDataItems.get(i).mailto.replaceAll(",", "\r\n");

			// 本文の改行コードを<br>に置換
			noticemailDataItems.get(i).content = noticemailDataItems.get(i).content.replaceAll("\n", "<br>");
		}

       //レコードの総数
	   Long allDataCntl = mailhistoryService.dataCount(loginDataDto.getTrackdataid(), noticetypeid);
	   int allDataCnt = Integer.parseInt(allDataCntl.toString());
	   int nowPage = Integer.parseInt(mailhistoryForm.numPage);

       if (allDataCnt != 0){
    	   createLink(nowPage, allDataCnt, pageDataCnt);
       }

	//トークンを設定
	//TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		setupModel(model);
		return "/page/mailhistory/index";
	}

    /**
     * ページャーの作成
     * @param nowPage 現在ページ
     * @param allDataCnt レコードの総数
     * @param pageDataCnt 1ページあたりの表示数
     * @return true
     */
    public Boolean createLink(int nowPage, int allDataCnt, int pageDataCnt) {
    	//全ページ数取得, allDataCntをpageDataCntで割り、余りが出たらその分の1ページ追加
        	if(allDataCnt % pageDataCnt == 0){
        		pageNum = allDataCnt / pageDataCnt;
        	}else{
        		pageNum = (allDataCnt / pageDataCnt) + 1;
        	}

        //前回分リセット
        links.clear();
        if(pageNum >= 11){
	        for( int i=0; i <= pageNum+1; i++) {
	            HashMap<String,String> link = new HashMap<String,String>();
		            //現在ページの場合
		            if( i==nowPage ) {
		            	link.put("numPage", String.format("%d", nowPage));
		            	link.put("type", "current");
		            //最初のページリンク
		            } else if(i == 0) {
			            link.put("numPage", lang.__("First page"));
			            link.put("type", "fst");
			        //最後のページリンク
		            } else if(i == pageNum+1) {
		            	link.put("numPage", lang.__("Last page"));
		            	link.put("type", "fnl");
		            //10件表示のため過ぎたページリンク表示しない
		            } else if(i <= nowPage-6 && nowPage >= 7 && nowPage <= pageNum-5) {
		            //現在ページが総ページ数-4以上の場合、総ページ-10ページ目を省略表示にする
		            } else if(i == pageNum-10 && nowPage >= pageNum-4) {
		            	link.put("numPage", "...");
				        link.put("type", "omission");
				    //現在ページが総ページ数-4以上の場合、総ページ数-11ページ目以下を表示しない
		            } else if(i <= pageNum-11 && nowPage >= 7 && nowPage >= pageNum-4) {
		            //10件表示のため過ぎたページリンクを省略表示("..."が複数表示されないように1つのみ指定)
		            } else if(i == nowPage-5 && nowPage >= 6 && nowPage <= pageNum-5){
		            	link.put("numPage", "...");
				        link.put("type", "omission");
		            //現在ページが6ページより小さい場合、10ページまでを表示する
		            } else if(nowPage < 6 && i <= nowPage+(10-nowPage)) {
		            	link.put("numPage", String.format("%d", i));
		            	link.put("type", "link");
		            //現在ページが6ページ以上の場合、1ページずつ追加
		            } else if(nowPage >= 6 && i < nowPage+6) {
		            	link.put("numPage", String.format("%d", i));
		            	link.put("type", "link");
		            //現在ページが総ページ数-6ページ目以下の場合、省略表示("..."が複数表示されないように1つのみ指定)
		            } else if(i==pageNum && nowPage <= pageNum-6) {
			            link.put("numPage", "...");
			            link.put("type", "omission");
		            //現在ページが総ページ数-5ページ以上の時、総ページ-9ページ以上を表示
		            } else if(i >= pageNum-9 && nowPage >= pageNum-5) {
		            	link.put("numPage", String.format("%d", i));
		            	link.put("type", "link");
		            }
	            links.add(link);
	        }
        }else {
	        	for( int i=0; i <= pageNum+1; i++) {
		            HashMap<String,String> link = new HashMap<String,String>();
			            //現在ページの場合
			            if( i==nowPage ) {
			            	link.put("numPage", String.format("%d", nowPage));
			            	link.put("type", "current");
			            } else if(i == 0) {
			            	link.put("numPage", lang.__("First page"));
			            	link.put("type", "fst");
			            } else if(i == pageNum+1) {
			            	link.put("numPage", lang.__("Last page"));
			            	link.put("type", "fnl");
			            } else {
			            	link.put("numPage", String.format("%d", i));
			            	link.put("type", "link");
			            }
		            links.add(link);
	            }
        }
        return true;
    }
}