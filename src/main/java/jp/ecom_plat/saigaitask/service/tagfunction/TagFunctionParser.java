/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.tagfunction;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;

import jp.ecom_plat.saigaitask.service.tagfunction.node.TFNCommaList;
import jp.ecom_plat.saigaitask.service.tagfunction.node.TFNNot;
import jp.ecom_plat.saigaitask.service.tagfunction.node.TagFunctionNode;

/**
 * タグ関数用のパーサ
 */
public class TagFunctionParser {

    private StreamTokenizer st;
    private TagFunctionToken tokenList;
    private TagFunction tagFunction;

    /**
     * コンストラクタ
     * 
     * @param in タグ関数文字列
     */
    public TagFunctionParser(TagFunction tagFunction, String in) {
    	this.tagFunction = tagFunction;
        Reader r = new StringReader(in);
        st = new StreamTokenizer(r);
        st.parseNumbers();
        st.ordinaryChar('.');
        st.ordinaryChar(':');
        st.ordinaryChar('/');
        st.ordinaryChar('<');
        st.ordinaryChar('>');
        st.wordChars('_', '_');
        st.quoteChar('"');
        tokenList = new TagFunctionToken();
    }

    /**
     * パースを行う
     * 
     * @return　解析結果（TagFunctionNode インスタンス）
     * @throws TagFunctionException
     */
    public TagFunctionNode parse() throws TagFunctionException {
        makeTokenArray(tagFunction);
        return parse(tokenList.next);    
    }

    /**
     * 字句解析（StreamTokenizer を使用）
     */
    private void makeTokenArray(TagFunction tagFunction) throws TagFunctionException {
        TagFunctionToken token = new TagFunctionToken("(", 0, null, TagFunctionToken.KEYWORD, tagFunction);
        tokenList = token;
        
        try {
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                switch (st.ttype) {
                case StreamTokenizer.TT_WORD:
                	// NOT INCLUDE を１つのトークンにする
                	if ("INCLUDE".equalsIgnoreCase(st.sval) && token.node instanceof TFNNot) {
                		token.remove();
                		token.removeOp();
                		token = new TagFunctionToken("NOT INCLUDE", 0, token.prev, TagFunctionToken.IDENTIFIER, tagFunction);
                		break;
                	}
                    token = new TagFunctionToken(st.sval, 0, token, TagFunctionToken.IDENTIFIER, tagFunction);
                    break;
                case StreamTokenizer.TT_NUMBER:
                    token = new TagFunctionToken("" + st.nval, st.nval, token, TagFunctionToken.NUMBER, tagFunction);
                    break;
                case '"':
                	token = new TagFunctionToken(st.sval, 0, token, TagFunctionToken.STRING, tagFunction);
                	break;
                case StreamTokenizer.TT_EOL:
                case StreamTokenizer.TT_EOF:
                    throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
                case '=':
                    if (token.type == TagFunctionToken.KEYWORD && (
                            token.text.equals(">") || token.text.equals("<") || token.text.equals("!")))
                        // >=, <=, != を１つのトークンにする
                        token = new TagFunctionToken(token.text + (char) st.ttype, 0, token.prev, TagFunctionToken.KEYWORD, tagFunction);
                    else
                        token = new TagFunctionToken("" + (char)st.ttype, 0, token, TagFunctionToken.KEYWORD, tagFunction);
                    break;
                case '>':
                    if (token.type == TagFunctionToken.KEYWORD && token.text.equals("<"))
                        // <> を１つのトークンにする
                        token = new TagFunctionToken(token.text + (char) st.ttype, 0, token.prev, TagFunctionToken.KEYWORD, tagFunction);
                    else
                        token = new TagFunctionToken("" + (char)st.ttype, 0, token, TagFunctionToken.KEYWORD, tagFunction);
                    break;
                case '(':
                	if (token.type == TagFunctionToken.IDENTIFIER)
                		// 関数の処理
                		token = new TagFunctionToken(token.text, 0, token.prev, TagFunctionToken.FUNCTION, tagFunction);
               		token = new TagFunctionToken("" + (char)st.ttype, 0, token, TagFunctionToken.KEYWORD, tagFunction);
                	break;
                default:
                    token = new TagFunctionToken("" + (char)st.ttype, 0, token, TagFunctionToken.KEYWORD, tagFunction);
                    break;
                }
            }
            
        } catch (IOException e) {
            throw new TagFunctionException(TagFunctionException.INTERNAL_ERROR);
        }
        
        new TagFunctionToken(")", 0, token, TagFunctionToken.KEYWORD, tagFunction);
    }

   /**
     * 構文解析
     * 
     * @param token TagFunctionToken インスタンス
     * @return 解析結果（TagFunctionNode インスタンス）
     * @throws TagFunctionException
     */
    private TagFunctionNode parse(TagFunctionToken token) throws TagFunctionException {
       while (true) {
            TagFunctionToken next = token.nextOp;
            if (next == null || token.info.leftPrecedance <= next.info.rightPrecedance) {
                
                // カッコ () は対応する TagFunctionNode がないため、ここで処理する
                if (token.text.equals("(")) {
                    if (token.next == null)
                        throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
                    
                    // () は空リストとみなす
                    if (token.next.text.equals(")")) {
                    	token.text = ",";
                    	token.node = new TFNCommaList();
	                    token.next.remove();
	                    token.nextOp.removeOp();
	                    token.removeOp();
                    }
                    
                    // 空でない場合
                    else if (token.next.next != null && token.next.next.text.equals(")")) {
	                    token.next.next.remove();
	                    token.nextOp.removeOp();
	                    token.remove();
	                    token.removeOp();
	                    if (token.prevOp == null)
	                        return token.next.node;
                    }
                    else
                        throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
                    token = token.prevOp;
                    continue;
                }
                
                // 対応する TagFunctionNode サブクラスに処理を依頼する
                else if (token.node != null) {
                    token.node.init(token);
                    if (token.prevOp == null)
                    	throw new TagFunctionException(TagFunctionException.INTERNAL_ERROR);
                    token = token.prevOp;
                    continue;
                }
                
                else
                    throw new TagFunctionException(TagFunctionException.PARSE_ERROR);
            }
            token = token.nextOp;
        }
    }

}
