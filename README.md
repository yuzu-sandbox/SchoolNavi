#SchoolNavi
時間割、行事予定などの学校の情報を管理する学校情報アプリです

学校の作品展用に作成したアプリで  
自分1人ではなく、チームで作りました

##機能一覧
###時間割確認
ユーザーは自分のクラスを登録し  
登録されたクラスの時間割を表示します。  
###学校行事カレンダー
Googleカレンダーのカレンダーに登録されている学校の行事予定を表示します  
###教室検索機能
時間割データを利用し  
授業で使われていない自習に使用できる教室を検索できます  
###ニュース機能
学校からの連絡をプッシュ通知を利用してお知らせします  

##使用環境
Android  
Java  
Mysql  
Google Cloud Message  
Google Calendar  

##注意
プッシュ通知のためのプロジェクト番号や  
通信するサーバーのURLなどを削除しているため  
_cloneだけでは使用することができません_

###変更点
CalendarQueryApiクラス  
userid - Google  
userpw  

CalendarAccessクラス  
url - GoogleCalenderURL  

connectionDBクラス  
url - サーバーurl  
user - データベース  
pass  

GoogleAccessクラス  
projectnum - Google Cloud Console  

SearchClassRoomクラス  
url - データベース  
