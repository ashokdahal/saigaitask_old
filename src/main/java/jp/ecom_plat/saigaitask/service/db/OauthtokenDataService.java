/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.oauthtokenData;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import jp.ecom_plat.map.oauth2.OAuth2Provider;
import jp.ecom_plat.saigaitask.entity.db.OauthtokenData;
import jp.ecom_plat.saigaitask.service.AbstractService;

/**
 * OAuthトークンデータのサービス.
 * DBへのアクセス、トークン発行を行う.
 */
@org.springframework.stereotype.Repository
public class OauthtokenDataService extends AbstractService<OauthtokenData> {

	/**
	 * @param id ID
	 * @return 検索結果
	 */
	public OauthtokenData findById(Long id) {
		return select().id(id).getSingleResult();
	}

	/**
	 * @param requestToken リクエストトークン
	 * @return 検索結果
	 */
	public OauthtokenData findByRequestToken(String requestToken) {
		return select().where(
				eq(oauthtokenData().requestToken(), requestToken)
				)
				.getSingleResult();

	}

	/**
	 * @param accessToken アクセストークン
	 * @return 検索結果
	 */
	public OauthtokenData findByAccessToken(String accessToken) {
		return select().where(
				eq(oauthtokenData().accessToken(), accessToken)
				)
				.getSingleResult();
	}

	/**
	 * @param consumerKey OAuthクライアントID (班ID利用)
	 * @param groupid 班ID
	 * @return アクセストークンが空でないもののリスト
	 */
	public List<OauthtokenData> findAccessTokenIsNotNullAndGroupId(String consumerKey, long groupid) {
		return select().where(
				eq(oauthtokenData().consumerKey(), consumerKey),
				eq(oauthtokenData().groupid(), groupid),
				isNotNull(oauthtokenData().accessToken()))
				.getResultList();

	}

	/**
	 * @param consumerKey OAuthクライアントID (課ID利用)
	 * @param groupid 班ID
	 * @return アクセストークンが空でないもののリスト
	 */
	public List<OauthtokenData> findAccessTokenIsNotNullAndUnitId(String consumerKey, long unitid) {
		return select().where(
				eq(oauthtokenData().consumerKey(), consumerKey),
				eq(oauthtokenData().unitid(), unitid),
				isNotNull(oauthtokenData().accessToken()))
				.getResultList();

	}

	/**
	 * リクエストトークンを発行してDBに登録する.
	 * @param consumerKey OAuthクライアントID
	 * @return 発行結果
	 */
	public OauthtokenData issueRequestToken(String consumerKey) {
		String token = generateToken(consumerKey);
		String secret = generateToken(token);

		OauthtokenData oauthtokenData = new OauthtokenData();
		oauthtokenData.consumerKey = consumerKey;
		oauthtokenData.requestToken = token;
		oauthtokenData.tokenSecret = secret;
		oauthtokenData.accessToken = null;
		oauthtokenData.verifier = null;
		oauthtokenData.created = new Timestamp(System.currentTimeMillis());
		insert(oauthtokenData);
		return oauthtokenData;
	}

	/**
	 * 認可コードを発行してDBに登録する.(班ID利用)
	 * @param requestToken リクエストトークン
	 * @param groupId 班ID
	 * @return 発行結果
	 */
	public OauthtokenData issueVerifierByGroupId(String requestToken, Long groupId) {

		OauthtokenData oauthtokenData = findByRequestToken(requestToken);
		if(oauthtokenData!=null) {
			oauthtokenData.verifier = generateVerifier();
			oauthtokenData.groupid = groupId;
			oauthtokenData.lastAccess = new Timestamp(System.currentTimeMillis());
			update(oauthtokenData);
		}

		return oauthtokenData;
	}

	/**
	 * 認可コードを発行してDBに登録する.(課ID利用)
	 * @param requestToken リクエストトークン
	 * @param groupId 班ID
	 * @return 発行結果
	 */
	public OauthtokenData issueVerifierByUnitId(String requestToken, Long unitId) {

		OauthtokenData oauthtokenData = findByRequestToken(requestToken);
		if(oauthtokenData!=null) {
			oauthtokenData.verifier = generateVerifier();
			oauthtokenData.unitid = unitId;
			oauthtokenData.lastAccess = new Timestamp(System.currentTimeMillis());
			update(oauthtokenData);
		}

		return oauthtokenData;
	}

	/**
	 * 認可コードを発行する.(班ID利用)
	 * @param consumerKey consumer_key
	 * @param groupId 班ID
	 * @return requestToken+"_"+verifier as authorization_code
	 */
	public String issueAuthorizationCodeByGroupId(String consumerKey, Long groupId) {

		// issue request_token
		OauthtokenData oauthtokenData = issueRequestToken(consumerKey);
		String requestToken = oauthtokenData.requestToken; // issue request_token

		// issue verifier_code
		oauthtokenData = issueVerifierByGroupId(requestToken, groupId);
		String verifier = oauthtokenData.verifier;

		// issue authorization_code from requestToken+"_"+verifier
		String authorizationCode = OAuth2Provider.getAuthorizationCode(requestToken, verifier);
		logger.info("issue authorization_code. code="+authorizationCode+", id="+oauthtokenData.id+", authId="+oauthtokenData.groupid+", consumer_key="+oauthtokenData.consumerKey);
		return authorizationCode;
	}

	/**
	 * 認可コードを発行する.(課ID利用)
	 * @param consumerKey consumer_key
	 * @param groupId 班ID
	 * @return requestToken+"_"+verifier as authorization_code
	 */
	public String issueAuthorizationCodeByUnitId(String consumerKey, Long unitId) {

		// issue request_token
		OauthtokenData oauthtokenData = issueRequestToken(consumerKey);
		String requestToken = oauthtokenData.requestToken; // issue request_token

		// issue verifier_code
		oauthtokenData = issueVerifierByUnitId(requestToken, unitId);
		String verifier = oauthtokenData.verifier;

		// issue authorization_code from requestToken+"_"+verifier
		String authorizationCode = OAuth2Provider.getAuthorizationCode(requestToken, verifier);
		logger.info("issue authorization_code. code="+authorizationCode+", id="+oauthtokenData.id+", authId="+oauthtokenData.unitid+", consumer_key="+oauthtokenData.consumerKey);
		return authorizationCode;
	}

	/**
	 * 認可コードからアクセストークンを発行する.
	 * @param authorizationCode 認可コード
	 * @return 発行結果
	 */
	public OauthtokenData issueAccessToken(String authorizationCode) {
		// get request_token, verifier_code from authorization_code
		String requestToken = OAuth2Provider.getRequestTokenByAuthorizationCode(authorizationCode);
		String verifier = OAuth2Provider.getVerifierByAuthorizationCode(authorizationCode);

		// check verifier
		OauthtokenData oauthtokenData = findByRequestToken(requestToken);
		if(oauthtokenData==null || oauthtokenData.verifier.equals(verifier)==false) {
			// if invalid verifier
			oauthtokenData.verifier = null;
			oauthtokenData.lastAccess = new Timestamp(System.currentTimeMillis());
			update(oauthtokenData);
			return oauthtokenData;
		}

		// issue access_token
		boolean isGroup = oauthtokenData.groupid!=null;
		if(isGroup)
			return issueAccessTokenByGroupId(oauthtokenData.consumerKey, oauthtokenData.groupid);
		else
			return issueAccessTokenByUnitId(oauthtokenData.consumerKey, oauthtokenData.unitid);
	}

	/**
	 *
	 * @param consumerKey OAuthクライアントID (班ID利用)
	 * @param groupid 班ID
	 * @return 発行結果
	 */
	public OauthtokenData issueAccessTokenByGroupId(String consumerKey, long groupid) {
		// already issue
		List<OauthtokenData> oauthtokenDatas = findAccessTokenIsNotNullAndGroupId(consumerKey, groupid);
		if(0<oauthtokenDatas.size()) return oauthtokenDatas.get(0);

		// issue
		// for now use md5 of name + current time as token
		String token = generateToken(consumerKey);

		OauthtokenData oauthtokenData = new OauthtokenData();
		oauthtokenData.consumerKey = consumerKey;
		oauthtokenData.requestToken = null;
		oauthtokenData.accessToken = token;
		oauthtokenData.groupid = groupid;
		oauthtokenData.verifier = null;
		oauthtokenData.created = new Timestamp(System.currentTimeMillis());
		insert(oauthtokenData);

		return oauthtokenData;
	}

	/**
	 *
	 * @param consumerKey OAuthクライアントID (課ID利用)
	 * @param groupid 班ID
	 * @return 発行結果
	 */
	public OauthtokenData issueAccessTokenByUnitId(String consumerKey, long unitid) {
		// already issue
		List<OauthtokenData> oauthtokenDatas = findAccessTokenIsNotNullAndUnitId(consumerKey, unitid);
		if(0<oauthtokenDatas.size()) return oauthtokenDatas.get(0);

		// issue
		// for now use md5 of name + current time as token
		String token = generateToken(consumerKey);

		OauthtokenData oauthtokenData = new OauthtokenData();
		oauthtokenData.consumerKey = consumerKey;
		oauthtokenData.requestToken = null;
		oauthtokenData.accessToken = token;
		oauthtokenData.unitid = unitid;
		oauthtokenData.verifier = null;
		oauthtokenData.created = new Timestamp(System.currentTimeMillis());
		insert(oauthtokenData);

		return oauthtokenData;
	}

	/**
	 * @return 生成したVerifierコード
	 */
	public static String generateVerifier() {
		return String.format("%1$06d", ( new Random() ).nextInt( 999999 ) );
	}

	/**
	 * @param consumerKey OAuthクライアントID
	 * @return 生成したトークン
	 */
	public static String generateToken(String consumerKey) {
		// for now use md5 of name + current time as token
		String token_data = consumerKey + System.nanoTime();
		String token = DigestUtils.md5Hex(token_data);
		return token;
	}
}
