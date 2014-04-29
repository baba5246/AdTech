# -*- coding: utf-8 -*- 


import sys
sys.path.append('./MeCab/MeCab-python/mecab-python-0.996')

import MeCab

class Mecab:
	"""docstring for """
	@classmethod
	def extract(self, string):
		m = MeCab.Tagger ("-Ochasen")
		return m.parse (string)

