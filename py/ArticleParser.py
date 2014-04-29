# -*- coding: utf-8 -*-

import sys
from HTMLParser import HTMLParser


class ArticleParser(HTMLParser):
  def __init__(self):
    HTMLParser.__init__(self)
    self.article_text_flag = 0
    self.article_text = []

  # aタグのみ処理を行い、href属性の内容をlinkurlに格納
  def handle_starttag(self, tag, attrs):
    if tag == 'div':
      attrs = dict(attrs)
      if 'class' in attrs:
        if attrs['class'] == 'articleText':
        	self.article_text_flag = 1
      elif self.article_text_flag > 0:
        self.article_text_flag = self.article_text_flag + 1

  # これは書かなくてもよい
  def handle_endtag(self, tag):
    if tag == 'div':
    	if self.article_text_flag > 0:
        	self.article_text_flag = self.article_text_flag - 1

  # linkurlに値が入っている場合のみ、（つまりAタグの場合）
  # urlをキー：アンカーテキストをバリューとしてディクショナリに追加
  def handle_data(self, data):
  	if self.article_text_flag > 0:
  		self.article_text.append(data)

	




