# -*- coding: utf-8 -*-

import sys
import urllib
import urllib2

class URL2HTML:
	"URLからHTMLをGETする"
	@classmethod
	def getHTML(self,url):
		fp = urllib2.urlopen(url)
		html = fp.read()
		fp.close()
		return html