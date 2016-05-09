package com.example.administrator.pictureclipdemo;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageCacheUtil {
	
	/**
	 * ת��ͼƬ��Բ��
	 * 
	 * @param bitmap
	 *            ����Bitmap����
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;

			left = 0;
			top = 0;
			right = width;
			bottom = width;

			height = width;

			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;

			float clip = (width - height) / 2;

			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;

			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);// ���û����޾��

		canvas.drawARGB(0, 0, 0, 0); // ������Canvas

		// ���������ַ�����Բ,drawRounRect��drawCircle
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// ��Բ�Ǿ��Σ���һ������Ϊͼ����ʾ���򣬵ڶ�������͵��������ֱ���ˮƽԲ�ǰ뾶�ʹ�ֱԲ�ǰ뾶��
		// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint); // ��Mode.SRC_INģʽ�ϲ�bitmap���Ѿ�draw�˵�Circle

		return output;
	}
	
	/**
	 * ��ͼƬ�ڴ�����Ĵ���
	 */
	public static Bitmap getResizedBitmap(String path, byte[] data,
			Context context,Uri uri, int target, boolean width) {
		Options options = null;

		if (target > 0) {

			Options info = new Options();
			info.inJustDecodeBounds = false;
			
			decode(path, data, context,uri, info);
			
			int dim = info.outWidth;
			if (!width)
				dim = Math.max(dim, info.outHeight);
			int ssize = sampleSize(dim, target);

			options = new Options();
			options.inSampleSize = ssize;

		}

		Bitmap bm = null;
		try {
			bm = decode(path, data, context,uri, options);
		} catch(Exception e){
			e.printStackTrace();
		}
		return bm;

	}
	
	public static Bitmap decode(String path, byte[] data, Context context,
			Uri uri, Options options) {

		Bitmap result = null;

		if (path != null) {

			result = BitmapFactory.decodeFile(path, options);

		} else if (data != null) {

			result = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);

		} else if (uri != null) {
			ContentResolver cr = context.getContentResolver();
			InputStream inputStream = null;

			try {
				inputStream = cr.openInputStream(uri);
				result = BitmapFactory.decodeStream(inputStream, null, options);
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return result;
	}
	
	
	private static int sampleSize(int width, int target){	    	
	    	int result = 1;	    	
	    	for(int i = 0; i < 10; i++){	    		
	    		if(width < target * 2){
	    			break;
	    		}	    		
	    		width = width / 2;
	    		result = result * 2;	    		
	    	}	    	
	    	return result;
	    }
	
	/**
	 * ѹ��ͼƬ
	 * 
	 * 
	 */
		public static int max = 0;
		public static boolean act_bool = true;
		public static List<Bitmap> bmp = new ArrayList<Bitmap>();

		// ͼƬsd��ַ �ϴ�������ʱ��ͼƬ�������淽��ѹ���� ���浽��ʱ�ļ��� ͼƬѹ����С��100KB��ʧ��Ȳ�����
		public static List<String> drr = new ArrayList<String>();

		// TelephonyManager tm = (TelephonyManager) this
		// .getSystemService(Context.TELEPHONY_SERVICE);

		public static Bitmap revitionImageSize(String path) throws IOException {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(
					new File(path)));

			Options options = new Options();
			options.inJustDecodeBounds = true;
			// Bitmap btBitmap=BitmapFactory.decodeFile(path);
			// System.out.println("ԭ�ߴ�߶ȣ�"+btBitmap.getHeight());
			// System.out.println("ԭ�ߴ��ȣ�"+btBitmap.getWidth());
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			int i = 0;
			Bitmap bitmap = null;
			while (true) {
				if ((options.outWidth >> i <= 800)
						&& (options.outHeight >> i <= 800)) {
					in = new BufferedInputStream(
							new FileInputStream(new File(path)));
					options.inSampleSize = (int) Math.pow(2.0D, i);
					options.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(in, null, options);
					break;
				}
				i += 1;
			}
			// ������Ϊ����ʱͼƬ��ת
//			bitmap = Photo.photoAdapter(path, bitmap);
//			System.out.println("-----ѹ����ߴ�߶ȣ�" + bitmap.getHeight());
//			System.out.println("-----ѹ����ߴ��ȶȣ�" + bitmap.getWidth());
			return bitmap;
		}

		public static Bitmap getLoacalBitmap(String url) {
			try {
				FileInputStream fis = new FileInputStream(url);
				return BitmapFactory.decodeStream(fis); // /����ת��ΪBitmapͼƬ

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * 
		 * @param x
		 *            ͼ��Ŀ��
		 * @param y
		 *            ͼ��ĸ߶�
		 * @param image
		 *            ԴͼƬ
		 * @param outerRadiusRat
		 *            Բ�ǵĴ�С
		 * @return Բ��ͼƬ
		 */
		public static Bitmap createFramedPhoto(int x, int y, Bitmap image, float outerRadiusRat) {
			// ���Դ�ļ��½�һ��darwable����
			Drawable imageDrawable = new BitmapDrawable(image);

			// �½�һ���µ����ͼƬ
			Bitmap output = Bitmap.createBitmap(x, y, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			// �½�һ������
			RectF outerRect = new RectF(0, 0, x, y);

			// ����һ����ɫ��Բ�Ǿ���
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.RED);
			canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);

			// ��ԴͼƬ���Ƶ����Բ�Ǿ�����
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			imageDrawable.setBounds(0, 0, x, y);
			canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
			imageDrawable.draw(canvas);
			canvas.restore();

			return output;
		}
}
