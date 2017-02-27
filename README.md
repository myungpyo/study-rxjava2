# study-rxjava2 
Test cases for RX Java 2 specifications.

This is android project 
There are only JUnit testcases, although I've created this project as type of android.
(Some day This project will have android dependent RX implementation. maybe;; )

# Usage
Most of test cases extend BasePlayGround which offers locking functionality for each test
```Java
@Test
public void printStrings() throws Exception {
    Observable.from(new String[]{"test1", "test2", "test3"})
            .subscribeOn(Schedulers.io())
            .subscribe(new Observer<String>() {
                @Override
                public void onCompleted() {
                    log.debug("onCompleted");
                    stopWaitingForObservable();
                }

                @Override
                public void onError(Throwable e) {
                    log.debug("onError : {}", e.getMessage());
                    stopWaitingForObservable();
                }

                @Override
                public void onNext(String s) {
                    log.debug("onNext : {}", s);
                }
            });

    waitForObservable();
}
```
**waitForObservable()** blocks current thread until **stopWaitingForObservable()** is called.
