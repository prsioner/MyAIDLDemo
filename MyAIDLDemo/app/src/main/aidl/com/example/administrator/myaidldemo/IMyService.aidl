// IMyService.aidl
package com.example.administrator.myaidldemo;

// Declare any non-default types here with import statements
import com.example.administrator.myaidldemo.Person;
interface IMyService {
        void savePersonInfo(in Person person);
        List<Person> getAllPerson();
}
