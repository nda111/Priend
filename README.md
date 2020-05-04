# Coding standard

## Java
### 1.  This coding standard have its base on [this document](https://source.android.com/setup/contribute/code-style).

### 2.  Coding standard for class name
- It is recommended to write components name by its purpose.
- Class name should be noun written in *PascalCasing*.
```
(X) fruit, animal, webSocket, web_socket
(O) Fruit, Animal, WebSocket
```
- Abstract class name should postfixed with 'Base'.

### 4. Coding standard for interface name
- Interface name should be adjective written in *PascalCasing*, prefixed with 'I'.
```
(X) Person, enumerableObject, serializable
(O) IEnumerable, ISerializable
```

### 5. Coding standard for method
- Method name should either be verb or verb noun combination written in *camelCasing*
```
(X) String GettingName()
(O) String getName()
```
- If might return null, postfix with 'OrNull'
```
public String getNameOrNull() {
    ...
}
```
- Getter Methods
  - It should be public method
  - Method name should be prefixed with “get”
  - It should not take any argument
- Setter Methods
  - It should be public method
  - Return Type should be void
  - Method name should be prefixed with “set”
  - It should take some argument

### 6. Coding convention for Listeners
- To register a Listener method name should prefixed with add.
- To unregister a Listener method name should prefixed with remove.

### 7. Coding convention for comment
- JavaDoc for class and interface requires:
  - ```@deprecated``` if deprecated
  - ```@author```
  - ```@since```
  - JavaDoc for method and construtor requires:
  - ```@deprecated``` if deprecated
  - ```@param``` if have parameters
  - ```@throws``` if throws exception
  - ```@return``` if returns value
  - ```@since```
- JavaDoc is mandatory for:
  - Class, Interface
  - public Fields, public Methods
  - protected Fields, protected Methods
  - Methods in Interface
- JavaDoc is optional for:
  - private Fields, private Methods
- It's recommended to write simple comment for local variables if needed.
- Comment should written in above Fields, Methods and variables.
- Comment should written in end of line inside Method.

### 8. Structural convention
```
// package

// import 

// Class or Interface header {

    // static Fields
    // static Methods

    // private nested Class
    // protected nested Class
    // public nested Class

    // private nested Interface
    // protected nested Interface
    // public nested Interface

    // private Fields
    // protected Fields
    // public Fields

    // private Constructors
    // protected Constructors
    // public Constructors

    // get/set Methods

    // private Methods
    // protected Methods
    // public Methods
}
```

## XML
### 1. Coding standard for Element id
- Layout resource id should use following format.
```
<layout name>_<object type>_<object name>
```
```
(X) button1, sendButton
(O) new_animal_button_register
```

### 2. Follow IDE default format
```
<?xml  version="1.0"  encoding="utf-8"?> 
<LinearLayout 
    xmlns:android="http://schemas.android.com/apk/res/android" 
    android:layout_width="match_parent" 
    android:layout_height="match_parent" > 

    <Button
        android:id="@+id/new_animal_button_register" 
        android:layout_width="wrap_content" 
        android:layout_height="wrap_content" />
</LinearLayout> 
```
