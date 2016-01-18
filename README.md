# RxComboDetector
Android view click combo detector with Rx.

## ScreenShot

![combo-demo.gif](art/combo-demo.gif)

## Usage
Add to gradle dependency of your module build.gradle:

```gradle
repositories {
    maven {
        url  "http://dl.bintray.com/piasy/maven" 
    }
}

dependencies {
    compile 'com.github.piasy:rxcombodetector:1.0.0'
}
```

Use in code:

```java
new RxComboDetector.Builder()
    .detectOn(mBtnCombo)
    .start()
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Action1<Integer>() {
        @Override
        public void call(Integer combo) {
            mTextView.setText(mTextView.getText() + "Combo x " + combo + "\n");
            mScrollView.fullScroll(View.FOCUS_DOWN);
            mYOLOComboView.combo(combo);
        }
    });
```

See [full example](https://github.com/Piasy/RxComboDetector/tree/master/app) for more details.

## Acknowledgements
+  `ViewClickOnSubscribe` and `Preconditions` classes are grabed from [RxBinding](https://github.com/JakeWharton/RxBinding), to reduce dependencies of this library.
+  Thanks for [rebound](https://github.com/facebook/rebound) to let me make fancy animation.
+  Thanks for [YOLO](https://www.yoloyolo.tv/), the yello smiling face.
