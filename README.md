## はじめに
- 本リポジトリはJava学習者の「ぴこ」(Xアカウント: @pikogappa)が作ったMineCraftプラグイン「MobFriend」に関するものです
- Java学習者が作ったデモ要素の強いプラグインとなりますので、ご利用いただくことでのトラブル等は一切責任を負いかねます
## コンセプト
- 私の子どもが「こんなMineCraftのゲームをつくってほしい」という要望をもとに、要件定義・設計・実装をしました！
- どこかで見たことあるようなゲームですが、温かい目でご覧いただければ幸いです
## ゲーム概要
- 本ゲームでは決められたモブを仲間（フレンド）にして育成や戦闘ができるゲームです
- MineCraft内のコマンド機能で決められたコマンド（「/CaptureFriendCommand」等）を入力することで、フレンドの「捕獲」、「表示」、「バトル」、「お別れ」等を行えます
- データベースとの連携機能は実装していないため、サーバからログアウトすると本ゲームに関するデータが消える点にご注意ください
## デモ動画
https://github.com/user-attachments/assets/513cb0f1-b48b-4d40-abe1-b66bc93b802a
## 環境
- 開発言語: Java Oracle OpenJDK 17.0.10
- アプリケーション: Minecraft　1.20.4
- サーバ：Spigot　1.20.4
- 検証済みOS: Mac OS 14.5（23F79）
## 利用方法
- gradle shadowJar等で、本プラグインのfat Jarを作成してください
- ご自身のMinecraftサーバーの「plugins」フォルダの中に先ほど作成したfat Jarファイルを追加してください
- サーバーおよびMineCraftを起動し、「/CaptureFriendCommand」等のコマンドが実行できるかご確認ください
## コマンド
- CaptureFriendCommand
  - フレンドを捕獲するコマンドです
  - コマンドを実行すると「金のりんご」がインベントリに付与され、目の前に現れる「ホグリン」、「イルカ」、「シロクマ」の3匹のうち1匹に右クリックで与えることで「フレンド」にできます
- ShowFriendCommand
  - フレンドの名前・ステータスを表示できるコマンドです
  - フレンドのアイコン（卵）にカーソルをあてると、ステータスを確認できます
- BattleFriendCommand
  - フレンドと敵モブで戦闘を行うコマンドです
  - 敵モブに勝っても負けても経験値を獲得できますが、勝利時の方が経験値を多くもらえます
  - 一定の経験値を獲得するとフレンドがレベルアップし、ステータスが上昇します
  - 確率10％でフレンドの攻撃がクリティカル攻撃になります
- ByeFriendCommand
  - フレンドとお別れするコマンドです
  - 実行後は再度Captureコマンドを実行し、新しいフレンドを捕獲できます
## 制約
- フレンドにできるモブの候補は最初の3匹のみで、それ以外のモブに金のリンゴを挙げても「このモブは仲間にできません」という旨のメッセージが表示されます
- またフレンドにできるモブは1匹のみとなるので、フレンドがいる状態でcaptureコマンドで追加で捕獲をしようとしても「フレンドがいます」という旨のメッセージが表示されます
- フレンドがいない状態で他のコマンドを実行しようとしても「フレンドがいません」という旨のメッセージが表示されます
- 戦闘相手はゾンビのみです
## 実装予定の機能
- 育成モードの実装
  - 戦闘以外にも経験値を獲得できるモード
- 戦闘モードの拡張
  - 敵モブの種類の追加
  - 必殺技などの戦闘エフェクトの追加 
- 仲間にできるモブの種類や数の拡張
- セーブ・ロード機能の実装
## おわりに
- Java学習者のアウトプットして、リポジトリ公開させていただきました
- 感想・コメント等あればXアカウントまでご連絡くださると幸いです
