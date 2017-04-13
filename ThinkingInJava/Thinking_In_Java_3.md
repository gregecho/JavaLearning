## 正文 ##
### 1. 抽象类 ###
>+ Java专门提供了一种机制，名为“抽象方法”。它属于一种不完整的方法，只含有一个声明，没有方法主体。
>+ 包含了抽象方法的一个类叫作“抽象类”。如果一个类里包含了一个或多个抽象方法，类就必须指定成abstract（抽象）。否则，编译器会向我们报告一条出错消息。

### 2. 接口 ###
>+ 一个接口的所有字段都自动具有static和final属性。
>+ 初始化接口中的字段:由于字段是static的，所以它们会在首次装载类之后、以及首次访问任何字段之前获得初始化。
>+ 字段并不是接口的一部分，而是保存于那个接口的static存储区域中。


### 3.内部类 ###
java中可以将一个类的定义放在另一个类的内部，这种叫做内部类。
内部类允许编程人员将逻辑上相关的类组织在一起，并且控制内部类对其他类的可见性。

#### 3.1 .this 与 .new ####
在外部类的非静态方法中创建内部类的对象语法:
>外部类类名.内部类类名 对象名 = 外部类对象.new 内部类类名();
````java
public class DotNew{
    public class Inner{}
    public static void main(String[] args){
        DotNew dn = new DotNew();
        DotNew.Inner dni = dn.new Inner();
    }
}
````
如果需要生成对外部类对象的引用，可以使用外部类的雷子后加.this.
````java
pubic class DotThis{
    void f(){ System.out.println("DotThis.f()");}
    public class Inner{
        public DotThis outer(){
            return DotThis.this;
        }
    }
    public Inner inner(){ return Inner();}
    public static void main(String[] args){
        DotThis dt = new DotThis();
        DotThis.Inner dti = dt.inner();
        dti.outer().f();
    }
}
````
**Note:** 在拥有外部类之前不可能创建内部类对象。因为内部类对象会暗暗的链接到创建他的外部类对象上。

#### 3.2 方法内部类 ####
内部类还可以定义在方法中，如：
````java
public Outer{
    public Inner getInner(){
        class Inner{
            public void f(){
                System.out.println("Method inner class");
            }
        }
        return new Inner();
    }
    public void fout(){
        Inner inner = getInner();
        inner.f();
    }
}
````
#### 3.3 匿名内部类 ####
Java中匿名内部类的应用十分广泛，所谓的匿名内部类就是指所创建的内部类没有类名称，也就是不知道内部类的具体类型，如：
````java
public class Outter{
    public Inner getInner(){
        return new Inner(){
            private String name = "inner";
            public String getName(){
                return name;
            }
        };
    }
}
````
匿名内部类传递final参数：如果在创建匿名内部类，需要外部类传递参数时，参数必须是final类型的，否则，编译时会报错。
````java
public class Outter{  
    public Inner getInne(final String name){
        return new Inner(){
            public String getName(){
                return name;
            }
        }
    } 
}  
````
注意：**如果外部类传递的参数在内部类中使用，则必须是final类型**，如果没有在内部类中使用(如，仅在基类中使用)，则可以不用是final类型的。

#### 3.4 嵌套类 ####
为正确理解static在应用于内部类时的含义，**必须记住内部类的对象默认持有创建它的那个封装类的一个对象的句柄**。然而，假如我们说一个内部类是static的，这种说法却是不成立的。static内部类意味着：
>+ 为创建一个static内部类的对象，我们不需要一个外部类对象。
>+ 不能从static内部类的一个对象中访问非静态的外部类对象。
但在存在一些限制：由于static成员只能位于一个类的外部级别，所以内部类不可拥有static数据或static内部类。

### 4.为什么要使用内部类 ###
#### 4.1 解决java中类不能多继承的问题 ####
Java中的继承是单继承，在某些情况下接口的多继承可以解决大部分类似C++中多继承的问题，但是如果一个类需要继承两个父类而不是接口，在java中是没法实现这种功能的，内部类可以帮助我们部分解决这种问题，如：
````java
abstract class A{  
    abstract public void f();  
}  
abstract class B{  
    abstract public void g();  
}  
public class D extends A{  
    public void f(){}  
    public B makeB(){  
        return new B(){  
            public void g(){};  
        }  
    }  
}
````
这样既继承了A类，有在makeB方法中使用匿名内部类继承了B类。
#### 4.2 闭包的问题 ####
**在面向对象中有个术语叫闭包，所谓闭包是指一个可调用的对象，它记录了一些信息，这些信息来自于创建它的作用域。**Java中不支持闭包，但是java的内部类可以看作是对闭包的一种解决方案，因为外部类的所有元素对于普通的内部类来说都是可见的，并且内部类还包含一个指向外部类的对象引用，因此在作用域内，内部类有权操作外部类的所有成员包括private成员。如：

------------
Todo:
* [ ] java内部类 **vs** c#内部类
* [ ] java内部类闭包
