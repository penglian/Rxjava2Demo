package com.example.a10346.rxjava2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void test(){
        //被观察者
        Observable observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                //ObservableEmitter 发射器  用于发送事件
            }
        });
        //观察者
        Observer observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                //Disposable可用于解除绑定  d.dispose()
            }
            @Override
            public void onNext(String o) {
                //默认执行的方法
            }
            @Override
            public void onError(Throwable e) {
                //只会执行一次与onComplete互斥,不在执行后面的操作
            }
            @Override
            public void onComplete() {
                //只会执行一次与onError互斥,不在执行后面的操作
            }
        };
        //绑定形成订阅关系
        observable.subscribe(observer);

        //-----------------------操作符---------------------
        //创建一个Observable，可接受一个或多个参数，将每个参数逐一发送
        Observable.just("hello world").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);  //打印hello
            }
        });
        //fromArray:创建一个Observable，接受一个数组，并将数组中的数据逐一发送
        //fromIterable：</b>创建一个Observable，接受一个可迭代对象，并将可迭代对象中的数据逐一发送
        //range：</b>创建一个Observable，发送一个范围内的整数序列
        //Observable.range(1,10);//从1开始以步长1递增发送10个数据
        Observable.range(0,5).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer); //依次打印0-5
            }
        });


        //filter：filter使用Predicate 函数接口传入条件值，来判断Observable发射的每一个值是否满足这个条件，如果满足，则继续向下传递，如果不满足，则过滤掉。
        Observable.range(0,10).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer % 3 == 0;  //判断条件  为true则传递给Observer
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);  //打印0，3，6，9
            }
        });
        //distinct:过滤掉重复的数据项，过滤规则为：只允许还没有发射过的数据项通过。
        Observable.just(1,1,1,2,3,3,4,4,5).distinct().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);  //打印1，2，3，4，5
            }
        });
        //当然也可以将filter与distinct进行组合链式使用
        Observable.just(1,1,1,2,3,3,4,4,5,6).distinct().filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer % 3 == 0;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);  //3，6
            }
        });

        //map:对Observable发射的每一项数据应用一个函数，执行变换操作
        //map操作符，需要接收一个函数接口Function<T,R>的实例化对象，实现接口内R apply(T t)的方法，在此方法中可以对接收到的数据t进行变换后返回。
        Observable.range(0,5).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "item:"+integer * integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s); //打印item:0，item:1，item:4，item:9，item:16，item:25
            }
        });
        //flatMap:将一个发射数据的Observable变换为多个Observable，然后将多个Observable发射的数据合并到一个Observable中进行发射
        Integer[] num1 = new Integer[]{1,2,3,4};
        Integer[] num2 = new Integer[]{5,6};
        Integer[] num3 = new Integer[]{7,8,9};
        Observable.just(num1, num2, num3).flatMap(new Function<Integer[], Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer[] integers) throws Exception {
                return Observable.fromArray(integers);
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer); //依次打印1-9
            }
        });


        //mergeWith：合并多个Observable发射的数据，可能会让Observable发射的数据交错。
        Integer []num4 = new Integer[]{1,2,3,4,5};
        Observable.just(5,6,7,8).mergeWith(Observable.fromArray(num4)).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);  //打印1,2,3,5,6,4,7,8  可能会发生错乱
            }
        });
        //concatWith：同mergeWith一样，用以合并多个Observable发射的数据，但是concatWith不会让Observable发射的数据交错。
        Integer []num5 = new Integer[]{1,2,3,4,5};
        Observable.just(5,6,7,8).concatWith(Observable.fromArray(num4)).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);  //打印1,2,3,4,5,6,7,8 不会错乱
            }
        });

        //zipWith：将多个Obversable发射的数据，通过一个函数BiFunction对对应位置的数据处理后放到一个新的Observable中发射，所发射的数据个数与最少的Observabel中的一样多。
        String []colors = new String[]{"黄色","红色","绿色","橙色","褐色","黑色"};
        Observable.just(1,2,3,4,5,6,7).zipWith(Observable.fromArray(colors), new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);  //打印1黄色 2红色 3绿色...6黑色
            }
        });

        //----------------------------------------背压-----------------------------
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for(int i = 0; i<5; i++){
                    System.out.println("发射线程:"+Thread.currentThread().getName()+"--->发射"+i);
                    Thread.sleep(1000);
                    e.onNext(i);
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer) throws Exception {
                        System.out.println("处理线程:"+Thread.currentThread().getName()+"--->处理"+integer);
                        return integer;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println("接收线程:"+Thread.currentThread().getName()+"--->接收"+integer);
                    }
                });

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                int i = 0;
                while(true){
                    i++;
                    e.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Thread.sleep(3000);
                        System.out.println(integer);
                    }
                });

        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for(int i = 0; i<500; i++){
                    e.onNext(i);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER).observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println(integer);
                    }
                });

        //onBackpressureXXX背压操作符
        //Flowable除了通过create创建的时候指定背压策略，也可以在通过其它创建操作符just，fromArray等创建后通过背压操作符指定背压策略。
        //onBackpressureBuffer()对应BackpressureStrategy.BUFFER
        //onBackpressureDrop()对应BackpressureStrategy.DROP
        //onBackpressureLatest()对应BackpressureStrategy.LATEST
        Flowable.range(0, 500)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println(integer);
                    }
                });
        //这个与上面create方式创建效果是一样的

        //背压最佳策略
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                int i = 0;
                while(true){
                    if(e.requested() == 0) continue;   //此处添加代码，让Flowable按需添加代码
                    System.out.println("发射--->"+i);
                    i++;
                    e.onNext(i);
                }
            }
        }, BackpressureStrategy.MISSING)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Integer>() {
                    Subscription subscription;
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(1);   //设置初始请求数据为1
                        subscription = s;
                    }
                    @Override
                    public void onNext(Integer integer) {
                        try {
                            Thread.sleep(1000);
                            System.out.println("接收--->"+integer);
                            subscription.request(1);//每接收到一条请求就增加一条请求量
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
