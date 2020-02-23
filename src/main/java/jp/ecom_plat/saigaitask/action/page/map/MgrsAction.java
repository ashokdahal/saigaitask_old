/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import jp.ecom_plat.map.image.WMSImage;
import jp.ecom_plat.map.servlet.ServletUtil;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.map.util.MapUtils;
import jp.ecom_plat.map.util.MgrsUtil;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.form.page.map.WmsAuthForm;

import org.apache.commons.lang.ArrayUtils;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.seasar.framework.util.StringUtil;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * MGRSグリッドのWMSサービスアクション.
 * spring checked take 5/14
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class MgrsAction extends AbstractAction {

	protected WmsAuthForm wmsAuthForm;

	static Color royalBlueColor = new Color(65, 105, 225);
	
	private static BufferedImage nullTile;
	static {
		nullTile = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * MGRSグリッドのWMSサービスアクション.
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/map/mgrs")
	@ResponseBody
	public byte[] index() {
		//<div lang="ja">小文字でパラメータマップ作成</div>
		//<div lang="en">Create parameter map in lowercase</div>
		Map<String, String> paramMap = FormUtils.getLowerCaseParameterMap(request);
		String wmsRequest = FormUtils.getStringParameter(request, paramMap.get("request"));
		if (wmsRequest == null) {
			try {
				ServletUtil.printError(response, 500, "invalid Request : "+wmsRequest);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new byte[0];
		}

		try {
			int imgWidth  = FormUtils.getIntParameter(request, paramMap.get("width"));
			int imgHeight = FormUtils.getIntParameter(request, paramMap.get("height"));
			//String srs = FormUtils.getURLEncordedParameter(request, paramMap.get("srs"));
			double[] bbox = FormUtils.getBboxParameter(request, paramMap.get("bbox"));
			//double buffer = FormUtils.getDoubleParameter(request, paramMap.get("buffer"));
			double buffer = 0d;
			boolean union = FormUtils.getBooleanParameter(request, "union");
			
			float strokeWidth = FormUtils.getFloatParameter(request, paramMap.get("swidth"), 1.0f);
			Color strokeColor = FormUtils.getHexColorParameter(request, paramMap.get("scolor"), royalBlueColor);
			Color fillColor = FormUtils.getHexColorParameter(request, paramMap.get("fcolor"));
			int precision  = FormUtils.getIntParameter(request, paramMap.get("precision"));

			String layers = FormUtils.getStringParameter(request, paramMap.get("layers"));
			if(StringUtil.isNotEmpty(layers)) {
				String[] elems = layers.split(",");
				for(int p=5; 0<=p; p--) {
					if(ArrayUtils.indexOf(elems, "mgrs"+p)!=-1) {
						precision = p;
						break;
					}
				}
			}

			//縮尺計算
			double xResolution = (bbox[2]-bbox[0])/imgWidth;
			double yResolution = (bbox[3]-bbox[1])/imgHeight;
			double resolution = Math.max(xResolution, yResolution);
			double scale = MapUtils.getScaleFromnResolution(resolution);
			double[] scales = new double[]{ 1.0E12, 1.0E11, 1.0E10, 1.0E9, 1.0E8, 1.0E7  };
			
			//logger.info("scale: "+scale);
			//logger.info("scales["+precision+"]: "+ scales[precision]);
			
			if(scale > scales[precision]){
				logger.debug("invalid scale: "+scale+" > "+ scales[precision]);
				return outputPngImage(nullTile);
				//return null;
			}
			
			//bboxに含まれるMGRSコード
			Vector<String> vecMgrs = MgrsUtil.getMGRSs(bbox, precision);
			
			//MGRSコードに対応するPolygonをハッシュに保存
			Hashtable<String, Geometry> hash = MgrsUtil.getPolygons(vecMgrs,precision,900913);

			BufferedImage image = null;
			try{
				image = WMSImage.createSearchRangeImage(bbox, imgWidth, imgHeight, "EPSG:4326",
						buffer, union, true,
						strokeWidth, strokeColor, fillColor,
						new Vector<Geometry>(hash.values()), null);
				
				//地図座標 → 画像へのAffinTransform
				CoordinateReferenceSystem crs = CRS.decode("EPSG:900913");
				AffineTransform worldToImageTransform = RendererUtilities.worldToScreenTransform(new Envelope(bbox[0],bbox[2],bbox[1],bbox[3]), new Rectangle(imgWidth, imgHeight), crs);
				
				//MGRSコードを記入
				int fontSize = 16;
				Graphics2D g = (Graphics2D) image.getGraphics();
				Font font = new Font(java.awt.Font.SANS_SERIF, Font.BOLD, fontSize);
				g.setFont(font);

				for(String mgrs : vecMgrs){
					Geometry geometry = hash.get(mgrs);
					if(geometry == null) continue;

					String position = "split";
					if("split".equalsIgnoreCase(position)) {
						// 桁数指定がある場合は、X座標コードを下側、Y座標コードを左側に表示する
						if(0<precision) {
							// 0のコードか判定するパターン
							String regex = "0*";
							Pattern p = Pattern.compile(regex);
							
							// 左側ラベル：Y座標コード
							String easting  = mgrs.substring(5,5+precision);
							Color fontColor = strokeColor;
							if(p.matcher(easting).matches()) fontColor = Color.RED;
							drawString(g, geometry, worldToImageTransform, easting, "leftline", fontColor);
							// 下側ラベル：X座標コード
							String northing = mgrs.substring(5+precision,5+precision*2);
							fontColor = strokeColor;
							if(p.matcher(northing).matches()) fontColor = Color.RED;
							drawString(g, geometry, worldToImageTransform, northing, "bottomline", fontColor);
						}
						// 桁数指定なしの場合は、ラベルにはゾーンを表示する
						else {
							drawString(g, geometry, worldToImageTransform, mgrs, "center", strokeColor);
						}
					}
					else {
						drawString(g, geometry, worldToImageTransform, mgrs, position, strokeColor);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
			
			return outputPngImage(image);
		} catch(Exception e) {
			throw new ServiceException(e);
		}

		//return null;
	}

	/**
	 * 矩形のポリゴンで指定位置に文字列を描画する。
	 * 文字列幅が納まらなければ描画されない。
	 * @param g
	 * @param geometry
	 * @param worldToImageTransform
	 * @param str
	 * @param position
	 * @param color
	 */
	static void drawString(Graphics2D g, Geometry geometry, AffineTransform worldToImageTransform, String str, String position, Color color) {
		FontMetrics fm = g.getFontMetrics();

		// 座標配列
		Coordinate[] points = geometry.getCoordinates();

		// テキスト幅
		int tw = fm.stringWidth(str);
		{
			Point2D p1 =  new Point2D.Double();
			Point2D p2 =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double(points[0].x , points[0].y), p1);
			worldToImageTransform.transform(new Point2D.Double(points[1].x , points[1].y), p2);

			// 文字が納まらなければ描画しない
			if( p1.distance(p2) <= tw ) return;
		}

		// 中心に描画
		Point center = geometry.getCentroid();
		// 左上のポイントを取得
		Coordinate tlPoint = null; // 左上
		Coordinate blPoint = null; // 左下
		Coordinate trPoint = null; // 右上
		Coordinate brPoint = null; // 右下
		for(Coordinate c : points) {
			if(c.x<center.getX() && c.y>center.getY()) tlPoint=c;
			if(c.x<center.getX() && c.y<center.getY()) blPoint=c;
			if(c.x>center.getX() && c.y>center.getY()) trPoint=c;
			if(c.x>center.getX() && c.y<center.getY()) brPoint=c;
		}

		// 表示位置
		float x, y;
		switch(position) {
		case "top": {
			// 中央、上寄せで描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double((tlPoint.x+trPoint.x)/2, (tlPoint.y+trPoint.y)/2), shape);
			// 表示位置
			x = (float)shape.getX() - tw/2;
			y = (float)shape.getY() + (fm.getHeight()/2);
			break;
		}
		case "bottom": {
			// 中央、下寄せで描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double((blPoint.x+brPoint.x)/2, (blPoint.y+brPoint.y)/2), shape);
			// 表示位置
			float offset = 5;
			x = (float)shape.getX() - tw/2;
			y = (float)shape.getY() - offset;
			break;
		}
		case "left": {
			// 中央、左寄せで描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double((tlPoint.x+blPoint.x)/2, (tlPoint.y+blPoint.y)/2), shape);
			// 表示位置
			float offset = 5;
			x = (float)shape.getX() + offset;
			y = (float)shape.getY() + (fm.getHeight()/2);
			break;
		}
		case "right": {
			// 中央、右寄せで描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double((trPoint.x+brPoint.x)/2, (trPoint.y+brPoint.y)/2), shape);
			// 表示位置
			float offset = 5;
			x = (float)shape.getX() - (tw+offset);
			y = (float)shape.getY() + (fm.getHeight()/2);
			break;
		}
		case "topline": {
			// 中央、上寄せで描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double((tlPoint.x+trPoint.x)/2, (tlPoint.y+trPoint.y)/2), shape);
			// 表示位置
			x = (float)shape.getX() - tw/2;
			y = (float)shape.getY() + (fm.getHeight()/4);
			break;
		}
		case "bottomline": {
			// 中央、下寄せで描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double((blPoint.x+brPoint.x)/2, (blPoint.y+brPoint.y)/2), shape);
			// 表示位置
			x = (float)shape.getX() - tw/2;
			y = (float)shape.getY() + (fm.getHeight()/4);
			break;
		}
		case "leftline": {
			// 中央、左寄せで描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double((tlPoint.x+blPoint.x)/2, (tlPoint.y+blPoint.y)/2), shape);
			// 表示位置
			x = (float)shape.getX() - (tw/2);
			y = (float)shape.getY() + (fm.getHeight()/2);
			break;
		}
		case "rightline": {
			// 中央、右寄せで描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double((trPoint.x+brPoint.x)/2, (trPoint.y+brPoint.y)/2), shape);
			// 表示位置
			x = (float)shape.getX() - (tw/2);
			y = (float)shape.getY() + (fm.getHeight()/2);
			break;
		}
		case "tl": {
			// 左上に描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double(tlPoint.x, tlPoint.y), shape);
			// 表示位置
			float offset = 5;
			x = (float)shape.getX() + offset;
			y = (float)shape.getY() + fm.getHeight();
			break;
		}
		case "bl": {
			// 左下に描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double(blPoint.x, blPoint.y), shape);
			// 表示位置
			float offset = 5;
			x = (float)shape.getX() + offset;
			y = (float)shape.getY() - offset;
			break;
		}
		case "tr": {
			// 右上に描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double(trPoint.x, trPoint.y), shape);
			// 表示位置
			float offset = 5;
			x = (float)shape.getX() - (tw+offset);
			y = (float)shape.getY() + fm.getHeight();
			break;
		}
		case "br": {
			// 右下に描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double(brPoint.x, brPoint.y), shape);
			// 表示位置
			float offset = 5;
			x = (float)shape.getX() - (tw+offset);
			y = (float)shape.getY() - offset;
			break;
		}
		case "center":
		default: {
			// 中心に描画
			Point2D shape =  new Point2D.Double();
			worldToImageTransform.transform(new Point2D.Double(center.getX(), center.getY()), shape);
			// 表示位置
			x = (float)shape.getX() - tw/2;
			y = (float)shape.getY();
			break;
		}}

		// 描画
		//下地色
		g.setColor(Color.WHITE);
		g.drawString(str,x+1,y+1);
		g.setColor(Color.WHITE);
		g.drawString(str,x-1,y-1);
		//フォント色
		g.setColor(color);
		g.drawString(str,x,y);
	}

	/**
	 * <div lang="ja"> 
	 * png画像をレスポンスに出力 。
	 * </div>
	 * 
	 * <div lang="en">
	 * Output png image to response.
	 * </div>   
	 * */
	static byte[] outputPngImage(BufferedImage image) throws IOException
	{
		//response.setContentType("image/png");
		//<div lang="ja">画像を出力</div>
		//<div lang="en">Output image</div>
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(bout);
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		ImageWriter iw = (ImageWriter) ImageIO.getImageWritersByFormatName("png").next();
		iw.setOutput(ios);
		iw.write(image);
		ios.close();
		out.close();//<div lang="ja">必須</div>
					//<div lang="en">Required</div>
		byte[] bytes = bout.toByteArray();
		bout.close();
		return bytes;
	}
}
