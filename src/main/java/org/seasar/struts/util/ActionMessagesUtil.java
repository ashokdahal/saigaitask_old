/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.struts.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import jp.ecom_plat.saigaitask.action.ServiceException;

/**
 * ActionMessages用のユーティリティです。
 * 
 * @author higa
 * @since 1.0.2
 */
public final class ActionMessagesUtil {

	/** 標準メッセージを Sessionに保存する変数名 */
	public static String MESSAGE_KEY = "messages";
	/** エラーメッセージを BindingResult に保存する変数名 */
	public static String ERROR_KEY = "action_errors";
	
    private ActionMessagesUtil() {
    }

    /**
     * @param actionMessages
     * @return ActionMessages を List<String> で取得
     */
    public static List<String> getMessagesList(ActionMessages actionMessages) {
    	List<String> messages = new ArrayList<>();
		@SuppressWarnings("unchecked")
		Iterator<ActionMessage> it = actionMessages.get();
		while(it.hasNext()) {
			ActionMessage message = it.next();
			messages.add(message.getKey());
		}
		return messages;
    }

    /*　*
     * エラーメッセージをリクエストに保存します。
     * 
     * @param request
     *            リクエスト
     * @param errors
     *            エラーメッセージ
     * @since 1.0.2
     * @deprecated Spring非対応
     *　/
    @Deprecated
    public static void saveErrors(HttpServletRequest request,
            ActionMessages errors) {
        if ((errors == null) || errors.isEmpty()) {
            request.removeAttribute(Globals.ERROR_KEY);
            return;
        }
        request.setAttribute(Globals.ERROR_KEY, errors);
    }*/

    /**
     * エラーメッセージをセッションに保存します。
     * 
     * @param session
     *            セッション
     * @param errors
     *            エラーメッセージ
     * @since 1.0.2
     */
    public static void saveErrors(HttpSession session, Set<String> errors) {
        if ((errors == null) || errors.isEmpty()) {
            session.removeAttribute(ERROR_KEY);
            return;
        }
        session.setAttribute(ERROR_KEY, errors);
    }

    /**
     * メッセージをリクエストに保存します。
     * ※Spring対応でメッセージをセッションに保存するので注意
     * 
     * @param request
     *            リクエスト
     * @param messages
     *            メッセージ
     * @since 1.0.2
     */
    public static void saveMessages(HttpServletRequest request,
            ActionMessages messages) {
    	// セッションに messages として保存
        saveMessages(request.getSession(), messages);
    }

    /**
     * メッセージをセッションに messages という変数名で保存します。
     * 
     * JSP例
     * <pre>
     *  <c:forEach var="msg" items="${messages}">
	 *	  <br/><span><c:out value="${f:h(msg)}" escapeXml="false"/></span>
	 *	</c:forEach>
	 *	<c:remove var="messages" scope="session"/>
	 * </pre>
     * 
     * <a href="http://www.jroller.com/raible/entry/migrating_from_struts_to_spring">Migrating from Struts to Spring</a>
     * 
     * @param session
     *            セッション
     * @param messages
     *            メッセージ
     * @since 1.0.2
     */
    public static void saveMessages(HttpSession session, ActionMessages actionMessages) {
    	// Spring 対応
        List<String> messages = getMessagesList(actionMessages);
        saveMessages(session, messages);
    }
    
    public static void saveMessages(HttpSession session, List<String> messages) {
    	// Spring 対応
		session.setAttribute(MESSAGE_KEY, messages);
    }

    /**
     * エラーメッセージをリクエストに追加します。
     * 
     * @param request
     *            リクエスト
     * @param errors
     *            エラーメッセージ
     * @since 1.0.2
     * @deprecated Spring非対応
     */
    @Deprecated
    public static void addErrors(HttpServletRequest request,
            ActionMessages errors) {
    	throw new ServiceException("ActionMessagesUtil.addErrors(bindingResult, errors) に移行してください！");
    }

    /**
     * エラーメッセージを追加します。
     * @param errorlist
     * @param errors
     */
    /*public static void addErrors(Set<String> errorlist, ActionMessages errors) {
        // Spring 対応
		@SuppressWarnings("unchecked")
		Iterator<ActionMessage> it = errors.get();
		while(it.hasNext()) {
			ActionMessage message = it.next();
			errorlist.add(message.getKey());
		}
    }*/
    
    /**
     * 
     * @param bindingResult
     * @param errors
     */
    public static void addErrors(BindingResult bindingResult,
            ActionMessages errors) {
        if (errors == null) {
            return;
        }
        
        // Spring 対応
		@SuppressWarnings("unchecked")
		Iterator<ActionMessage> it = errors.get();
		while(it.hasNext()) {
			ActionMessage message = it.next();
			ObjectError error = new ObjectError(ERROR_KEY, message.getKey());
			bindingResult.addError(error);
		}
    }

    /**
     * エラーメッセージをセッションに追加します。
     * 
     * @param session
     *            セッション
     * @param errors
     *            エラーメッセージ
     * @since 1.0.2
     */
    public static void addErrors(HttpSession session, ActionMessages errors) {
        if (errors == null) {
            return;
        }
        Set<String> sessionErrors = (Set<String>) session
                .getAttribute(ERROR_KEY);
        if (sessionErrors == null) {
            sessionErrors = new LinkedHashSet<String>();
        }
        sessionErrors.addAll(getMessagesList(errors));
        saveErrors(session, sessionErrors);
    }

    /* *
     * エラーメッセージがあるかどうかを返します。
     * 
     * @param request
     *            リクエスト
     * @return エラーメッセージがあるかどうか
     * @since 1.0.4
     * @deprecated Spring非対応
     * /
    @Deprecated
    public static boolean hasErrors(HttpServletRequest request) {
        ActionMessages errors = (ActionMessages) request
                .getAttribute(Globals.ERROR_KEY);
        if (errors != null && !errors.isEmpty()) {
            return true;
        }
        return false;
    }*/

    /**
     * メッセージをリクエストに追加します。
     * ※Spring対応でメッセージをセッションに保存するので注意
     * 
     * @param request
     *            リクエスト
     * @param messages
     *            メッセージ
     * @since 1.0.2
     */
    public static void addMessages(HttpServletRequest request,
            ActionMessages actionMessages) {
        if (actionMessages == null) {
            return;
        }

        // get or create
        List messages = (List) request.getSession().getAttribute(MESSAGE_KEY);
    	if (messages == null) {
    		messages = new ArrayList();
        }

    	// add
    	messages.addAll(getMessagesList(actionMessages));

    	// save
        saveMessages(request.getSession(), messages);
    }

    /**
     * メッセージをセッションに追加します。
     * 
     * @param session
     *            セッション
     * @param messages
     *            メッセージ
     * @since 1.0.2
     * @deprecated Spring非対応
     */
    @Deprecated
    public static void addMessages(HttpSession session, ActionMessages messages) {
    	/*
        if (messages == null) {
            return;
        }
        ActionMessages sessionMessages = (ActionMessages) session
                .getAttribute(Globals.MESSAGE_KEY);
        if (sessionMessages == null) {
            sessionMessages = new ActionMessages();
        }
        sessionMessages.add(messages);
        saveMessages(session, sessionMessages);
        */
    }
}
