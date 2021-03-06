# -*- coding: utf-8 -*-
"""2017312576_이동준_SVM.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/1WrAAiQJuRj1SyWZZqIQ9xAevBDLNDXnD
"""

import nltk, json
from nltk.tokenize import word_tokenize
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.metrics import confusion_matrix, precision_score, recall_score, f1_score, accuracy_score


nltk.download("punkt")
nltk.download('averaged_perceptron_tagger')

with open('./bbc_articles_train.json') as json_file:
  train_data = json.load(json_file)
train = train_data['business'] + train_data['tech'] + train_data['politics']
train_labels = (['business'] * 80) + (['tech'] * 80) + (['politics'] * 80) 

with open('./bbc_articles_test.json') as json_file:
  test_data = json.load(json_file)
test = test_data['business'] + test_data['tech'] + test_data['politics']
test_labels = (['business'] * 20) + (['tech'] * 20) + (['politics'] * 20) 

POS_tags = ['NN','NNS','NNP','NNPS','VB','VBD','VBG','VBN','VBP','VBZ']
POS_corpus = []
for sentence in train:
  pos_token = nltk.pos_tag(word_tokenize(sentence))
  POS_corpus.append(' '.join(t[0] for t in pos_token if t[1] in POS_tags))

tfidfvect = TfidfVectorizer()
tfidfvect.fit_transform(POS_corpus)
train_tf_idf = tfidfvect.transform(POS_corpus).toarray().tolist()

test_POS_corpus=[]
for sentence in test:
  pos_token = nltk.pos_tag(word_tokenize(sentence))
  test_POS_corpus.append(' '.join(t[0] for t in pos_token if t[1] in POS_tags))

test_tfidfvect = TfidfVectorizer()
test_tfidfvect.fit_transform(test_POS_corpus)
test_tf_idf = tfidfvect.transform(test_POS_corpus).toarray().tolist()

classifier = LinearSVC(C=1.0, max_iter=1000)
classifier.fit(train_tf_idf,train_labels)

predict = classifier.predict(test_tf_idf)
label = test_labels
conf_mat =  confusion_matrix(label,predict)
acc = ((accuracy_score(label, predict)*100)//0.0001)*0.0001

macro_precision = ((precision_score(label,predict,average='macro')*100)//0.0001)*0.0001
micro_precision = ((precision_score(label,predict,average='micro')*100)//0.0001)*0.0001

macro_recall = ((recall_score(label,predict,average='macro')*100)//0.0001)*0.0001
micro_recall = ((recall_score(label,predict,average='micro')*100)//0.0001)*0.0001

macro_f1 = ((f1_score(label,predict,average='macro')*100)//0.0001)*0.0001
micro_f1 = ((f1_score(label,predict,average='micro')*100)//0.0001)*0.0001



fw = open("./2017312576_이동준_SVM.txt",'w',encoding='UTF-8')
fw.write("Confusion matrix\n")
fw.write(str(conf_mat[0][0])+"\t"+str(conf_mat[0][1])+"\t"+str(conf_mat[0][2])+"\n")
fw.write(str(conf_mat[1][0])+"\t"+str(conf_mat[1][1])+"\t"+str(conf_mat[1][2])+"\n")
fw.write(str(conf_mat[2][0])+"\t"+str(conf_mat[2][1])+"\t"+str(conf_mat[2][2])+"\n\n")

fw.write("Accuracy : "+str(acc)[:7]+"%\n\n")
fw.write("Macro averaging precision : "+str(macro_precision)[:7]+"%\n")
fw.write("Micro averaging precision : "+str(micro_precision)[:7]+"%\n\n")
fw.write("Macro averaging recall : "+str(macro_recall)[:7]+"%\n")
fw.write("Micro averaging recall : "+str(micro_recall)[:7]+"%\n\n")
fw.write("Macro averaging f1-score : "+str(macro_f1)[:7]+"%\n")
fw.write("Micro averaging f1-score : "+str(micro_f1)[:7]+"%")

fw.close()

