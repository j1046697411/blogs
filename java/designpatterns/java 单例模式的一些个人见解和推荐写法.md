# 说说对单例模式的简单见解和推荐写法

## 先说说什么是单例模式吧？

&emsp;&emsp; 先说说自己的见解吧！我认为单例模式就是在程序生命周期内，在内存中指挥存在一个对象的实例。  
&emsp;&emsp;Java中单例模式定义：“一个类有且仅有一个实例，并且自行实例化向整个系统提供。”  ----出处 [单例模式](https://baike.baidu.com/item/%E5%8D%95%E4%BE%8B%E6%A8%A1%E5%BC%8F/5946627?fr=aladdin)

&emsp;&emsp;先说说几种常见的单例模式的写法吧！  
### 第一种懒汉模式：
```java
public class Singleton{
    private static final Singleton INSTANCE = new Singleton();

    public static Singleton getInstance(){
        return INSTANCE;
    }

}
```
&emsp;&emsp;优先：不存在多线线程问题，java的机制会保证对象在内存中只有一份。  
&emsp;&emsp;缺点：在类被加载的时候就直接初始话了，如果后面一直都使用的话就造成了内存浪费，如果初始比较消耗的话也会拖慢第一次加载。  
&emsp;&emsp;个人见解: 个人不太喜欢用这种方式，对象的初始化时间不太容易控制。  
&emsp;&emsp;推荐指数：  
&emsp;&emsp;单线程：★★☆☆☆  
&emsp;&emsp;多线程：★★☆☆☆  

### 第二种饿汉模式：
```java
public class Singleton{
    
    private static final Singleton INSTANCE;

    public static Singleton getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Singleton();
        }
        return INSTANCE;
    }

}
```
&emsp;&emsp;优先：在第一次调用方法的时候才会构造对象，解决了饿汉模式的内存浪费，和初始化不确定的问题。  
&emsp;&emsp;缺点：在多线程情况下无法保证内存对象只有一个，有肯能在内存多同时存在多个。造成程序错误。
（原因：java的复制和构造对象无法保证是原子操作，所有在多线程中，如果一个线程刚好进入if内，还没有完成对象赋值，这是又有线程到了if判断内
，因为第一个还没有完成赋值操作，所有现在INSTANCE对象还为null，这是第二个线程也会取构造一个对象，这样就会在线程内形成多个对象。）  
&emsp;&emsp;个人见解：个人觉得这种方式的单例容易造成在多线程中的问题，不太推荐。  
&emsp;&emsp;推荐指数：  
&emsp;&emsp;单线程：★★★☆☆  
&emsp;&emsp;多线程：☆☆☆☆☆  

### 第三种方法锁模式
这种模式就是在饿汉模式上在方法上加上方法锁
```java
public class Singleton{

    private static final Singleton INSTANCE;

    public static synchronized Singleton getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Singleton();
        }
        return INSTANCE;
    }

}
```
&emsp;&emsp;优先：解决了饿汉模式中多线程中可能造成程序错误的问题，也保留了延迟初始话的优点。  
&emsp;&emsp;缺点：每次调用方法都会先取获取锁，不管是不是在多线程或者有没有人在使用，都会需要获取锁，造成方法调用速度变慢。  
&emsp;&emsp;个人见解：这种方式还是不错的，解决了多线程问题，也做到了延迟初始化对象。唯一不足的地方就是每次调用方法，都需要获取锁，不是特别适合。
&emsp;&emsp;推荐指数：  
&emsp;&emsp;单线程：★☆☆☆☆  
&emsp;&emsp;多线程：★★★☆☆

### 第四种检查锁模式
```java
public class Singleton {

    private static final Singleton INSTANCE;

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            synchronized (Singleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }

}
```
&emsp;&emsp;优先：解决了每次调用方法都检查锁的问题，而且也保留了延迟初始化的有点  
&emsp;&emsp;缺点：实现稍微有点复制，而且不是很容易理解。  
&emsp;&emsp;个人见解：这是比较推荐的方式，这种方式解决了，上面几种方式中出现的问题。  
&emsp;&emsp;推荐指数：  
&emsp;&emsp;单线程：★★★★☆  
&emsp;&emsp;多线程：★★★★☆

### 第五种枚举模式
这种模式也许不太常见，但是的确存在这种方式，曾经在google的开源项目中发现过，个人认为还是不错的。这种方式和懒汉模式模式差不多吧
```java
public enum Singleton {
    INSTANCE;
}
```

&emsp;&emsp;优先：和饿汉模式差不多，java的枚举会保证对象的唯一，唯一直接支持序列化的模式  
&emsp;&emsp;缺点：和饿汉模式呢一样，存在初始化过早和不太确定的问题  
&emsp;&emsp;个人见解：还不错的模式吧，比饿汉模式简单  
&emsp;&emsp;推荐指数：  
&emsp;&emsp;单线程：★★★☆☆  
&emsp;&emsp;多线程：★★★★☆

### 第六种静态内部类模式
```java
public class Singleton {

    public static Singleton getInstance(){
        return Holder.SIN;
    }

    private static class Holder{
        private static Singleton SIN = new Singleton();
    }
}
```

&emsp;&emsp;优先：内部类保证了对象的延迟加载和对象的唯一性，只有在方法调用的时候才会被初始化，相比一检查锁模式有简单很多  
&emsp;&emsp;缺点：唯一的缺点就是会多加载一个类。    
&emsp;&emsp;个人见解：这种模式是个人最推荐的一种方，他保留了上面所以模式的优先，有把他们的缺点解决掉了。  
&emsp;&emsp;推荐指数：  
&emsp;&emsp;单线程：★★★★★  
&emsp;&emsp;多线程：★★★★★  

## Android 中的单例模式

额外在说说单例模式在Android中的一些应用和注意吧。

```java
public class SingletonApplication extends Application {
    private static SingletonApplication SIN;

    @Override
    public void onCreate() {
        super.onCreate();
        SIN = this;
    }


    public SingletonApplication getInstance(){
        return SingletonApplication.SIN;
    }
}

```
&emsp;&emsp;这应该是Android最常见的一种应用了吧！Application 对象在Android天生就是单例模式，这种与其说这种方式是应用，还不如说是单例的一种应用还不如说是一种扩展吧！  
&emsp;&emsp;但是单例在Android有特别容易造成内存泄漏，好多时候如果把activity或者view等一些对象传给了单例对象引用
了有没有在activity等对象释放的时候取置空引用的话，那内存泄漏就发生了，所以在Android中使用单例模式的时候
需要特别注意。需要记得每次都去释放对象。
&emsp;&emsp;比较推荐大家这样去持有一些容易造成泄漏的对象 `WeakReference<Context> contextWeakReference;`
适用弱引用的方式，这样就不会造成对象无法被回收了。

### 说在后面的话
&emsp;&emsp;谢谢大家看，以上都是自己作为一个Android 和 java 开发的一些见解，希望对大家会有一点作用把，
如果一在这其中哪怕有一点收获我也会觉得非常的开心。后面也会写一些自己比较喜欢的东西。

## links
[作者](https://github.com/j1046697411/)  