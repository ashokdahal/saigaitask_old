/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.extension.jdbc.operation.Operations;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovgroupmemberInfo;
import jp.ecom_plat.saigaitask.form.admin.setupper.LocalgovGroupForm;
import jp.ecom_plat.saigaitask.form.db.LocalgovgroupmemberInfoForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.db.LocalgovgroupInfoService;
import jp.ecom_plat.saigaitask.service.db.LocalgovgroupmemberInfoService;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 自治体セットアッパーの自治体グループ一覧・登録・編集画面のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class LocalgovGroupAction extends AbstractSetupperAction {

	/** アクションフォーム */
	protected LocalgovGroupForm localgovGroupForm;

	// Service
	@Resource protected LocalgovgroupInfoService localgovgroupInfoService;
	@Resource protected LocalgovgroupmemberInfoService localgovgroupmemberInfoService;

	/** 一覧表示するグループリスト */
	public List<LocalgovGroupForm> groups = new ArrayList<>();
	/** 登録・編集モードの親組織、子組織用のセレクトボックス */
	public Map<Long, String> localgovSelectOptionsAll;
	
	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("groups", groups);
		model.put("localgovSelectOptionsAll", localgovSelectOptionsAll);
	}

	protected Logger getLogger() {
		return Logger.getLogger(LocalgovGroupAction.class);
	}

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/localgovGroup","/admin/setupper/localgovGroup/index"})
	public String index(Map<String,Object>model,
			@ModelAttribute LocalgovGroupForm localgovGroupForm, BindingResult bindingResult) {
		this.localgovGroupForm = localgovGroupForm;

		// 管理者権限のないユーザの場合はウイザードページに遷移
		if(!loginDataDto.isAdmin()){
			return "forward:/admin/setupper/menuWizard/";
		}

		// 表示内容の取得
		content(model, localgovGroupForm, bindingResult);

		setupModel(model);
		return "/admin/setupper/localgovGroup/index";
	}

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ	
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/localgovGroup/content")
	public String content(Map<String,Object>model,
			@ModelAttribute LocalgovGroupForm localgovGroupForm, BindingResult bindingResult) {
		this.localgovGroupForm = localgovGroupForm;

		// セットアッパーの初期化
		initSetupper();

		// 二度押し防止トークン
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		// フィルタ自治体IDがない場合
		// システム管理者でログイン中
		if(loginDataDto.getGroupid()==0) {
			// ユーザが指定していれば、指定した自治体IDでフィルタ
			if(StringUtil.isNotEmpty(request.getParameter("filterlocalgovinfoid"))) {
				// ユーザが指定した自治体IDをそのまま利用する
			}
			// 特に指定がなければ、セットアッパーで選択中の自治体IDでフィルタ
			else {
				localgovGroupForm.filterlocalgovinfoid = String.valueOf(loginDataDto.getLocalgovinfoid());
			}
		}
		// システム管理者以外は自分の自治体のみ表示可能
		else {
			localgovGroupForm.filterlocalgovinfoid = String.valueOf(loginDataDto.getLocalgovinfoid());
		}
		
		// 親組織、子組織用のセレクトボックスを生成
		localgovSelectOptionsAll = getLocalgovSelectOptionsAll();
		localgovSelectOptionsAll.put(0L, lang.__("未指定"));
		
		// 自治体IDフィルター
		long filterlocalgovinfoid = Long.parseLong(localgovGroupForm.filterlocalgovinfoid);

		// 一覧表示の場合
		if(StringUtil.isEmpty(localgovGroupForm.id)) {
			// グループ情報の取得
			List<LocalgovgroupInfo> groupInfos = null;
			// すべて表示
			if(filterlocalgovinfoid==0L) {
				groupInfos = localgovgroupInfoService.findAll(Operations.asc(Names.localgovgroupInfo().disporder().toString()));
			}
			// 自治体IDでフィルタする
			else {
				groupInfos = localgovgroupInfoService.findByLocalgovinfoidOrderByDisporder(filterlocalgovinfoid);
			}

			// Form に変換
			for(LocalgovgroupInfo groupInfo : groupInfos) {
				LocalgovGroupForm form = new LocalgovGroupForm();
				Beans.copy(groupInfo, form).execute();
				// グループメンバー情報の取得
				setMemberForm(form);
				groups.add(form);
			}
		}
		else {
			// 新規登録モード
			if("0".equals(localgovGroupForm.id)) {
				// 親組織の初期値
				localgovGroupForm.localgovinfoid = String.valueOf(filterlocalgovinfoid);

				// 表示順の初期値
				//表示順取得
				int maxDisporder = localgovgroupInfoService.getLargestDisporder();
				maxDisporder += 10; // 10刻みにする
				localgovGroupForm.disporder = String.valueOf(maxDisporder);
			}
			// 編集モード
			else {
				LocalgovgroupInfo group = localgovgroupInfoService.findById(Long.parseLong(localgovGroupForm.id));
				Beans.copy(group, localgovGroupForm).execute();
				// グループメンバー情報の取得
				setMemberForm(localgovGroupForm);
			}
			
			// 子組織がない場合、新規登録用で１件だけ追加する
			if(localgovGroupForm.members.size()==0) {
				LocalgovgroupmemberInfoForm newmember = new LocalgovgroupmemberInfoForm();
				newmember.id = "0"; // 新規登録は 0
				newmember.localgovinfoid = "0"; // 初期値は 0:未指定
				newmember.disporder = "10"; // 初期値は 10
				localgovGroupForm.members.add(newmember);
			}
			
			// 一覧表示対応で、Formは groups にいれておく必要が有る。
			groups.add(localgovGroupForm);
		}

		setupModel(model);
		return "/admin/setupper/localgovGroup/content";
	}
	
	public void setMemberForm(LocalgovGroupForm form) {
		// グループメンバー情報の取得
		List<LocalgovgroupmemberInfo> members = localgovgroupmemberInfoService.findByLocalgovgroupinfoid(Long.parseLong(form.id));
		for(LocalgovgroupmemberInfo member : members) {
			LocalgovgroupmemberInfoForm memberForm = new LocalgovgroupmemberInfoForm();
			Beans.copy(member, memberForm).execute();
			form.members.add(memberForm);
		}
	}


	/**
	 * 保存実行（登録、編集）
	 *
	 * @return 遷移ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/localgovGroup/save")
	public String save(Map<String,Object>model,
			@Valid @ModelAttribute LocalgovGroupForm localgovGroupForm, BindingResult bindingResult) {
		this.localgovGroupForm = localgovGroupForm;

		if(bindingResult.hasErrors()) {
			//return "forward:/admin/setupper/initSetup/";
			return index(model, localgovGroupForm, bindingResult);
		}

		//boolean success = false;
		try {
			//Logger logger = getLogger();

			// トークンチェック
			if (!TokenProcessor.getInstance().isTokenValid(request, true)) {
				throw new ServiceException(lang.__("The request is invalid. Old request has been sent."));
			}

			boolean isInsert = "0".equals(localgovGroupForm.id);

			// 自治体グループ情報、メンバー情報
			LocalgovgroupInfo localgovGroup = new LocalgovgroupInfo();
			if(!isInsert) {
				localgovGroup.id = Long.parseLong(localgovGroupForm.id);
				localgovGroup = localgovgroupInfoService.findById(localgovGroup.id);
			}

			// 自治体グループ情報に入力パラメータを反映
			Beans.copy(localgovGroupForm, localgovGroup)
			.dateConverter("yyyy-MM-dd").execute();
			
			// 新規登録モード
			if(isInsert) {
				localgovgroupInfoService.insert(localgovGroup);
			}
			// 編集モード
			else {
				localgovgroupInfoService.update(localgovGroup);
			}

			// 自治体グループメンバー情報に入力パラメータを反映
			for(LocalgovgroupmemberInfoForm memberForm : localgovGroupForm.members) {
				LocalgovgroupmemberInfo member = new LocalgovgroupmemberInfo();
				Beans.copy(memberForm, member).dateConverter("yyyy-MM-dd").execute();
				member.localgovgroupinfoid=localgovGroup.id;
				// 新規登録の場合
				if(member.id==0) {
					if(member.localgovinfoid!=0) {
						localgovgroupmemberInfoService.insert(member);
					}
				}
				// 更新処理の場合
				else {
					// 0:未指定を選択の場合
					if(member.localgovinfoid==0) {
						localgovgroupmemberInfoService.delete(member);
					}
					// 更新を実行する
					else {
						localgovgroupmemberInfoService.update(member);
					}
				}
			}

		} catch (Exception e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("自治体グループの保存でエラーが発生しました。"), e);
			ActionMessages errors = new ActionMessages();
			addExceptionMessages(errors, e);

			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);
		//return "forward:/admin/setupper/localgovGroup/index";
		return "redirect:/admin/setupper/localgovGroup/index";
	}

	/**
	 * 削除実行
	 *
	 * @return 遷移ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/localgovGroup/delete")
	public String delete(Map<String,Object>model,
			@Valid @ModelAttribute LocalgovGroupForm localgovGroupForm, BindingResult bindingResult) {
		this.localgovGroupForm = localgovGroupForm;

		if(bindingResult.hasErrors()) {
			//return "forward:/admin/setupper/initSetup/";
			return index(model, localgovGroupForm, bindingResult);
		}

		//boolean success = false;
		try {
			//Logger logger = getLogger();

			// トークンチェック
			if (!TokenProcessor.getInstance().isTokenValid(request, true)) {
				throw new ServiceException(lang.__("The request is invalid. Old request has been sent."));
			}

			// 自治体グループ情報
			LocalgovgroupInfo localgovGroup = new LocalgovgroupInfo();
			localgovGroup.id = Long.parseLong(localgovGroupForm.id);
			localgovGroup = localgovgroupInfoService.findById(localgovGroup.id);
			if(localgovGroup!=null) {
				/* 物理削除の場合
				// メンバー情報の削除
				List<LocalgovgroupmemberInfo> members = localgovgroupmemberInfoService.findByLocalgovgroupinfoid(localgovGroup.id);
				for(LocalgovgroupmemberInfo member : members) {
					localgovgroupmemberInfoService.delete(member);
				}
				// グループ情報を削除
				localgovgroupInfoService.delete(localgovGroup);
				*/
				// 論理削除の場合
				localgovGroup.deleted = true;
				localgovgroupInfoService.update(localgovGroup);
			}
			
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("自治体グループの保存でエラーが発生しました。"), e);
			ActionMessages errors = new ActionMessages();
			addExceptionMessages(errors, e);

			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);
		//return "forward:/admin/setupper/localgovGroup/index";
		return "redirect:/admin/setupper/localgovGroup/index";
	}

	protected void addExceptionMessages(ActionMessages messages, Exception e) {
		Throwable t = e;
		while(t!=null) {
			if(t instanceof ServiceException) {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+t.getMessage(), false));
			}
			/*
			else if(t instanceof org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException) {
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+lang.__("The request failed due to the size of uploaded file over limitation."), false));
				messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("　・"+t.getMessage(), false));
			}
			*/
			t=t.getCause();
		}
	}
}
