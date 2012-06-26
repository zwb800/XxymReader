package com.reader.xxym;

import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public abstract class NewsContentClient {
	private Context context;

	public NewsContentClient(Context context) {
		this.context = context;
	}

	private NewsDao ndao;
	private ProgressDialog dialog;

	public NewsContentClient(Activity activity) {
		this.context = activity;
		ndao = new NewsDao(context);
		dialog = new ProgressDialog(this.context);
		dialog.setTitle("内容");
		dialog.setMessage("加载中...");
		dialog.setIndeterminate(true);
	}

	public void load(final String url, boolean cache) {
		Map<String, Object> news = ndao.getNewsContentByURL(url);
		if (cache) {
			if (news!=null&&news.containsKey("content") && news.get("content") != null) {
				fillResult(news.get("content").toString());
			} else {
				load(url, false);
			}
		} else {
			dialog.show();
			new Downloader(context) {
				@Override
				protected void onPostExecute(String result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					Pattern p = Pattern.compile(
							"<div class=\"article_p\">((\\s|\n|.)+?)</div>",
							Pattern.MULTILINE);

					Matcher m = p.matcher(result);
					if (m.find()) {
						MatchResult mr = m.toMatchResult();
						String content = mr.group(1);
						p = Pattern.compile("<br />", Pattern.MULTILINE);
						m = p.matcher(content);
						content = m.replaceAll("\n");
						p = Pattern.compile("<p />", Pattern.MULTILINE);
						m = p.matcher(content);
						content = m.replaceAll("\n");
						p = Pattern.compile("<[^>]+>", Pattern.MULTILINE);
						m = p.matcher(content);
						content = m.replaceAll("");
						content = content.replace("\r", "");
						p = Pattern.compile("(\n+)\\w", Pattern.MULTILINE);
						m = p.matcher(content);
						if (m.find() && m.find()) {
							int s = m.end(1);
							content = content.substring(s);
						}

						ndao.updateNews(url, content);
						fillResult(content);
						dialog.dismiss();
					}

				}
			}.execute(url, false);
		}
	}

	public abstract void fillResult(String content);
}
