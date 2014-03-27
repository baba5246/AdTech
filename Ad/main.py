# -*- coding: utf-8 -*-

import sys
sys.path.append('.')
import URL2HTML
import ArticleParser
import Mecab


# 入力されたURLを取得
argvs = sys.argv
argc = len(argvs)
if (argc != 2):   # 引数が足りない場合は、その旨を表示
	print 'Usage: # python %s url' % argvs[0]
	quit()         # プログラムの終了

url = argvs[1]
doc = URL2HTML.URL2HTML.getHTML(url)

article_text = ""

parser = ArticleParser.ArticleParser()
parser.feed(doc)
parser.close()
for text in parser.article_text:
	if text != "\r\n" and text != "\n" and text != "\n\n":
		text = text.replace('\r\n','')
		article_text += text # unicode(text, "utf-8")

keitaiso = Mecab.Mecab.extract(article_text)
print type(keitaiso)

