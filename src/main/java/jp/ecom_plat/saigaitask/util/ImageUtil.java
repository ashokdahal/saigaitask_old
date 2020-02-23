/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * <p>CVS: $Id: ImageUtil.java,v 1.1 2012/08/30 05:35:15 oku Exp $</p>
 */
public class ImageUtil {
	protected static SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

    /**
     * ファイル名のドットなし拡張子を得る
     * @param filename
     * @return jpg など
     */
    public static String getSuffix(String filename){
        int dotp = filename.lastIndexOf(".");
        return (dotp!=-1)?filename.substring(dotp+1):"";
    }


    /**
     * 画像ファイルの縦横の大きさを調べる
     * @param filePath ファイルのパス
     * @return intの配列 {width,height}
     */
    public static int[] getFileLength(final String filePath){
    	try{
	        File file = new File(filePath);
	        BufferedImage image = ImageIO.read(file);
	        return new int[]{image.getWidth(),image.getHeight()};
    	}catch(Exception e ) {
    		e.printStackTrace();
    		return null;
    	}
    }

    /**
     * 読み込み可能な画像ファイルかチェックする
     * 拡張子を見て判断する
     */
    public static boolean isReadable(File file) {
        if( file!=null && file.exists() ) {
            // 読み込めるかチェック
            String suffix = getSuffix(file.getName());
            String[] readerFormatNames = ImageIO.getReaderFormatNames();
            boolean readable = false;
            for( String formatName : readerFormatNames ) {
                if( formatName.equals(suffix) ) {
                    readable = true;
                    break;
                }
            }
            return readable;
        }
        else return false;
    }


    /**
     * 画像ファイルをリサイズする
     * @param file     File   リサイズしたい画像
     * @param destFile[] File   リサイズ画像の保存先
     * @param width[]    double リサイズ画像の最大横幅（ピクセル）
     * @param height[]   double リサイズ画像の最大高さ（ピクセル）
     * @param format   String リサイズ画像の保存形式（ImageIO.getWriterFormatNames()で確認）
     * @return int[ファイル数][2]、intの配列 {width,height} リサイズ失敗の時は null
     */
    public static int[][] resize(File file, File[] destFiles, double[] widths, double[] heights, String format){
        try{
            // 読み込めるかチェック
            if( isReadable(file) == false ) {
            	System.out.println(lang.__("File can not read")+file.getName()+")");
//                Logger.debug("image resize error: "+"読み込めないファイルです。("+file.getName()+")");
                return null;
            }

            // リサイズする画像を読み込む
            BufferedImage originalImage = ImageIO.read(file);

            if( destFiles!=null && 0<destFiles.length ) {
                int num = destFiles.length;
                int[][] sizes = new int[num][2];
                for( int i=0; i<num; i++ ) {
                    File destFile = destFiles[i];
                    double width = widths[i];
                    double height = heights[i];
                    int[] wh = resize(originalImage, destFile, width, height, format);
                    if( wh!=null ) {
                        sizes[i] = wh;
                    }
                }
                return sizes;
            }
            else return null;
        }
        catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 画像ファイルをリサイズする
     * @param file     File   リサイズしたい画像
     * @param destFile File   リサイズ画像の保存先
     * @param width    double リサイズ画像の最大横幅（ピクセル）
     * @param height   double リサイズ画像の最大高さ（ピクセル）
     * @param format   String リサイズ画像の保存形式（ImageIO.getWriterFormatNames()で確認）
     * @return intの配列 {width,height} リサイズ失敗の時は null
     */
    public static int[] resize(BufferedImage originalImage, File destFile, double width, double height, String format){
        try{
            // 書き込めるかチェック
            String[] writerFormatNames = ImageIO.getWriterFormatNames();
            boolean writable = false;
            String suffix = format;
            for( String formatName : writerFormatNames ) {
                if( formatName.equals(suffix) ) {
                    writable = true;
                    break;
                }
            }
            // 書き込めない
            if( writable==false ) {
            	System.out.println(lang.__("Format not available. ({0})", format));
  //              Logger.debug("image resize error: "+"書き込みできないフォーマットです。("+format+")");
                return null;
            }

            double scale;
            BufferedImage resizeImage = null;
            if( 0<width && 0<height ) {
                scale= Math.min((double)width/originalImage.getWidth(),(double)height/originalImage.getHeight());
                if( scale>1 ) scale = 1;
                resizeImage = new AffineTransformOp(AffineTransform.getScaleInstance(scale,scale),null).filter(originalImage,null);
                ImageIO.write(resizeImage,format,destFile);
//                Logger.debug("resize to: "+destFile.getPath());
            }
            else {
                ImageIO.write(originalImage,format,destFile);
                resizeImage = originalImage;
            }

            return new int[]{resizeImage.getWidth(),resizeImage.getHeight()};
        }
        catch( Exception e ) {
        	e.printStackTrace();
            //logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 画像ファイルをリサイズする
     * @param file     File   リサイズしたい画像
     * @param destFile File   リサイズ画像の保存先
     * @param width    double リサイズ画像の最大横幅（ピクセル）
     * @param height   double リサイズ画像の最大高さ（ピクセル）
     * @param format   String リサイズ画像の保存形式（ImageIO.getWriterFormatNames()で確認）
     * @return リサイズ 成功: true, 失敗: false
     */
    public static boolean resize(File file, File destFile, double width, double height, String format){
        try{
            // 読み込めるかチェック
            String suffix = getSuffix(file.getName());
            String[] readerFormatNames = ImageIO.getReaderFormatNames();
            boolean readable = false;
            for( String formatName : readerFormatNames ) {
                if( formatName.equals(suffix) ) {
                    readable = true;
                    break;
                }
            }
            // 読み込めない
            if( readable==false ) return false;

            // 書き込めるかチェック
            String[] writerFormatNames = ImageIO.getWriterFormatNames();
            boolean writable = false;
            for( String formatName : writerFormatNames ) {
                if( formatName.equals(suffix) ) {
                    writable = true;
                    break;
                }
            }
            // 書き込めない
            if( writable==false ) return false;

            // リサイズする画像を読み込む
            BufferedImage originalImage = ImageIO.read(file);
            double scale;

            if( 0<width && 0<height ) {
                scale= Math.min((double)width/originalImage.getWidth(),(double)height/originalImage.getHeight());
                if( scale>1 ) scale = 1;
                BufferedImage image = new AffineTransformOp(AffineTransform.getScaleInstance(scale,scale),null).filter(originalImage,null);
                ImageIO.write(image,format,destFile);
            }
            else {
                ImageIO.write(originalImage,format,destFile);
            }

        }
        catch( Exception e ) {
//            logger.error(e.getMessage(), e);
        	e.printStackTrace();
            return false;
        }
        return true;
    }

}
