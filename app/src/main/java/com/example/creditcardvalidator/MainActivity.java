package com.example.creditcardvalidator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getCanonicalName();

    Disposable isFormValidDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText expirationDateInput =
                (EditText) findViewById(R.id.expiration_date);
        EditText creditcardInput =
                (EditText) findViewById(R.id.credit_card_number);
        EditText cvcInput =
                (EditText) findViewById(R.id.credit_card_cvc);
        Button submitFormButton =
                (Button) findViewById(R.id.submit_button);

        /*
        Checks validity of expiration date
         */
        Observable<Boolean> isExpirationDateValid = getExpirationDateValidityObservable(expirationDateInput);

        /*
        Checks validity of credit card number entry
         */
        Observable<CharSequence> creditCardNumberObservable =
                RxTextView.textChanges(creditcardInput);

        Observable<CardType> cardTypeObservable =
                creditCardNumberObservable.map(CardType::fromString);

        Observable<Boolean> isCreditCardNumberValid =
                getCardNumberValidityObservable(creditCardNumberObservable, cardTypeObservable);

        /*
        Checks CVC length
         */
        Observable<Boolean> isCvcCodeValid =
                getCvcValidityObservable(cvcInput, cardTypeObservable);


        subscribeIsFormValid(submitFormButton, isExpirationDateValid, isCreditCardNumberValid, isCvcCodeValid);

    }

    private void subscribeIsFormValid(Button submitFormButton, Observable<Boolean> isExpirationDateValid, Observable<Boolean> isCreditCardNumberValid, Observable<Boolean> isCvcCodeValid) {
        Observable<Boolean> isFormValidObservable =
                ValidationUtils.and(
                        isCreditCardNumberValid
                        .doOnNext(value -> Log.d(TAG, "isCreditCardNumberValid : "+value)),
                        isCvcCodeValid
                                .doOnNext(value -> Log.d(TAG, "isCvcCodeValid : "+value)),
                        isExpirationDateValid
                                .doOnNext(value -> Log.d(TAG, "isExpirationDateValid : "+value)));

        isFormValidDisposable = isFormValidObservable
                .doOnNext(value -> Log.d(TAG, "isCreditCardNumberValid : "+value))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> throwable.printStackTrace())
                .onErrorReturnItem(false)
                .subscribe(submitFormButton::setEnabled);
    }

    private Observable<Boolean> getCvcValidityObservable(EditText cvcInput, Observable<CardType> cardTypeObservable) {
        Observable<CharSequence> cvcObservable =
                RxTextView.textChanges(cvcInput);

        Observable<Integer> requiredCVCLength =
                cardTypeObservable
                        .map(cardType -> cardType.getCvcLength());

        Observable<Integer> cvcInputLength =
                cvcObservable
                        .map(CharSequence::length);

        return ValidationUtils.equals(requiredCVCLength, cvcInputLength);
    }

    private Observable<Boolean> getCardNumberValidityObservable(Observable<CharSequence> creditCardNumberObservable, Observable<CardType> cardTypeObservable) {
        Observable<Boolean> isCardTypeValid =
                cardTypeObservable.map(cardType -> cardType != CardType.UNKNOWN);

        /*
        Checks the credit card's check sum
         */
        Observable<Boolean> isCheckSumValid =
                creditCardNumberObservable
                        .map(ValidationUtils::convertToInt)
                        .map(ValidationUtils::checkCardChecksum);

        return ValidationUtils.and(isCardTypeValid, isCheckSumValid);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!isFormValidDisposable.isDisposed())
            isFormValidDisposable.dispose();
    }

    private Observable<Boolean> getExpirationDateValidityObservable(EditText expirationDateInput) {
        Observable<CharSequence> expirationDateObservable =
                RxTextView.textChanges(expirationDateInput);
        return expirationDateObservable.map(ValidationUtils::checkExpirationDate);
    }
}
