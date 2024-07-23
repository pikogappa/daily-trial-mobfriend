## はじめに
- 本リポジトリは2024年2月からJava学習しているぴこ(@pikogappa)が作ったMineCraftプラグインに関するものです
- Java初心者が作ったデモ要素の強いプラグインですので、ご利用いただくことでのトラブル等は一切責任を負いかねますのでご了承ください
## コンセプト
- 私の子どもが「こんなゲームつくってほしい」というアイディアをもとに、要件定義・設計・実装しました！
- どこかで見たことあるようなゲームですが、温かい目でご覧いただければ幸いです
## MobFriendゲーム概要
- 本ゲームでは決められたモブを仲間（フレンド）にして育成や戦闘ができるゲームです
- MineCraft内のコマンド機能で決められたコマンド（「/CaptureFriendCommand」等）を入力することで、フレンドの「捕獲」、「表示」、「戦闘」、「お別れ」等を行えます
- データベースとの連携機能は実装していないので、一度サーバからログアウトすると本ゲームに関するデータが消える点にご注意ください
## デモ動画

## 環境
- 開発言語: Java SE
- アプリケーション: MineCraft
- サーバ：Spigot
- 検証済みOS: Mac  
## プラグインの利用方法
- gradle shadowJar等で、本プラグインのfat Jarを作成してください
- ご自身のMinecraftサーバーの「plugins」フォルダの中に先ほど作成したfat Jarファイルを追加してください
- サーバーおよびMineCraftを起動し、「/CaptureFriendCommand」等のコマンドが実行できるかご確認ください
## コマンド
- CaptureFriendCommand
  - フレンドを捕獲するコマンド
  - 目の前に現れる「ホグリン」、「イルカ」、「シロクマ」の3匹のうち、金のりんごを右クリックであげることで1匹をフレンドにできます
- ShowFriendCommand
  - フレンドの情報を閲覧できるコマンド
  - アイコンにカーソルをあてるとステータスを確認できます
- BattleFriendCommand
  - フレンドと敵モブで戦闘を行うコマンド
  - 倒しても負けても経験値を獲得できます
- ByeFriendCommand
  - フレンドとお別れするコマンド
  - 実行後は再度Captureコマンドを実行し、新しいフレンドを捕獲できます
## プラグインの制約について
- フレンドにできるモブの候補は最初の3匹のみで、それ以外のモブに金のリンゴを挙げても「このモブは仲間にできません」という旨のメッセージが表示されます
- またフレンドは1匹のみで、フレンドがいる状態で捕獲しようとしても「フレンドがいます」という旨のメッセージが表示されます
- フレンドがいない状態で他のコマンドを実行しようとしても「フレンドがいません」という旨のメッセージが表示されます
- 戦闘相手はゾンビのみです
## 今後作りたい機能
- 育成モードの実装
  - 戦闘以外にも経験値を獲得できるモード
- 戦闘モードの拡張
  - 敵モブの種類の追加
  - 必殺技などの戦闘エフェクトの追加 
- 仲間にできるモブの種類や数の拡張
- セーブ・ロード機能の実装
## おわりに
- 
