/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
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
package jp.ecom_plat.saigaitask.action.admin;

import org.springframework.web.bind.annotation.RequestMapping;

import jp.ecom_plat.saigaitask.action.AbstractAction;

/**
 * ログインページActionクラス
 *
 * @author take
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/admin")
public class IndexAction extends AbstractAction {

	@RequestMapping(value={"/admin","/admin/index"})
	public String index() {
		//return "/login?type=admin";
		return "forward:/admin/mainFrame";
	}
}
