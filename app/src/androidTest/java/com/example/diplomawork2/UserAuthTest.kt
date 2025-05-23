package com.example.diplomawork2

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserAuthTest {

    @get:Rule
    val loginActivityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testUserSignupAndLoginFlow() {
        // Переход на экран регистрации
        onView(withId(R.id.signupRedirect)).perform(click())

        // Проверка, что мы на экране регистрации (например, по кнопке регистрации)
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()))

        // Вводим имя пользователя и пароль
        onView(withId(R.id.signupUsername)).perform(typeText("testuser"), closeSoftKeyboard())
        onView(withId(R.id.signupPassword)).perform(typeText("password123"), closeSoftKeyboard())

        // Нажимаем кнопку регистрации
        onView(withId(R.id.signupButton)).perform(click())

        // После успешной регистрации должно перейти на экран входа
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))

        // Вводим логин и пароль для входа
        onView(withId(R.id.loginUsername)).perform(typeText("testuser"), closeSoftKeyboard())
        onView(withId(R.id.loginPassword)).perform(typeText("password123"), closeSoftKeyboard())

        // Нажимаем кнопку входа
        onView(withId(R.id.loginButton)).perform(click())

        // Проверяем, что после входа открывается SplashScreen (проверяем, что элемент SplashScreen отображается)
        // Здесь нужно проверить элемент, который есть на SplashScreen, например:
            //onView(withId(R.id.splashScreenRootLayout)).check(matches(isDisplayed()))
    }
}
