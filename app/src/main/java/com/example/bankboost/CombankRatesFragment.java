package com.example.bankboost;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class CombankRatesFragment extends Fragment {

    private Spinner combankSpinner;
    private ImageView combankRatesImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_combank_rates, container, false);

        combankSpinner = v.findViewById(R.id.combankSpinner);
        combankRatesImage = v.findViewById(R.id.combankRatesImage);

        // Creating items to display in the spinner
        String [] accounts = {"Select Account Type", "Savings Accounts", "Current Accounts", "Deposits", "Loans", "Elite Banking"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, accounts);
        combankSpinner.setAdapter(adapter);

        // Creating texts to display when the user select each banking type
        TextView messageText = v.findViewById(R.id.messageText);
        String savingsText = "Visit Here to view for more information about Savings Accounts!";
        String currentText = "Visit Here to view for more information about Current Accounts!";
        String depositsText = "Visit Here to view for more information about Deposits!";
        String loansText = "Visit Here to view for more information about Loans!";
        String eliteText = "Visit Here to view for more information about Elite Banking!";

        // Spinner for Commercial Bank to display the dropdown items and each of their  content
        combankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        combankRatesImage.setImageResource(0);
                        messageText.setText("To view the rates for each account category, please select your preferred option from the list above. We offer competitive rates to help you make the most of your finances, so take a look and see which option is right for you.");
                        messageText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        break;

                    case 1:
                        combankRatesImage.setImageResource(R.drawable.combanksavings);
                        SpannableString savingsMessage = new SpannableString(savingsText);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                showWebViewDialog("https://www.combank.lk/personal-banking/savings-accounts");
                            }
                        };
                        int start = savingsText.indexOf("Here");
                        int end = start + 4;
                        savingsMessage.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        savingsMessage.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        messageText.setText(savingsMessage);
                        messageText.setMovementMethod(LinkMovementMethod.getInstance());
                        messageText.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        break;

                    case 2:
                        combankRatesImage.setImageResource(R.drawable.combankcurrent);
                        SpannableString currentMessage = new SpannableString(currentText);
                        ClickableSpan clickableSpan2 = new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                showWebViewDialog("https://www.combank.lk/personal-banking/current-accounts");
                            }
                        };
                        int start2 = currentText.indexOf("Here");
                        int end2 = start2 + 4;
                        currentMessage.setSpan(clickableSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentMessage.setSpan(new StyleSpan(Typeface.BOLD), start2, end2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        messageText.setText(currentMessage);
                        messageText.setMovementMethod(LinkMovementMethod.getInstance());
                        messageText.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        break;

                    case 3:
                        combankRatesImage.setImageResource(R.drawable.combankdeposits);
                        SpannableString depositsMessage = new SpannableString(depositsText);
                        ClickableSpan clickableSpan3 = new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                showWebViewDialog("https://www.combank.lk/personal-banking/term-deposits");
                            }
                        };
                        int start3 = depositsText.indexOf("Here");
                        int end3 = start3 + 4;
                        depositsMessage.setSpan(clickableSpan3, start3, end3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        depositsMessage.setSpan(new StyleSpan(Typeface.BOLD), start3, end3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        messageText.setText(depositsMessage);
                        messageText.setMovementMethod(LinkMovementMethod.getInstance());
                        messageText.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        break;

                    case 4:
                        combankRatesImage.setImageResource(R.drawable.combankloans);
                        SpannableString loansMessage = new SpannableString(loansText);
                        ClickableSpan clickableSpan4 = new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                showWebViewDialog("https://www.combank.lk/personal-banking/loans");
                            }
                        };
                        int start4 = loansText.indexOf("Here");
                        int end4 = start4 + 4;
                        loansMessage.setSpan(clickableSpan4, start4, end4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        loansMessage.setSpan(new StyleSpan(Typeface.BOLD), start4, end4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        messageText.setText(loansMessage);
                        messageText.setMovementMethod(LinkMovementMethod.getInstance());
                        messageText.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        break;

                    case 5:
                        combankRatesImage.setImageResource(R.drawable.combankelite);
                        SpannableString eliteMessage = new SpannableString(eliteText);
                        ClickableSpan clickableSpan5 = new ClickableSpan() {
                            @Override
                            public void onClick(View view) {
                                showWebViewDialog("https://www.combank.lk/elite-banking");
                            }
                        };
                        int start5 = eliteText.indexOf("Here");
                        int end5 = start5 + 4;
                        eliteMessage.setSpan(clickableSpan5, start5, end5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        eliteMessage.setSpan(new StyleSpan(Typeface.BOLD), start5, end5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        messageText.setText(eliteMessage);
                        messageText.setMovementMethod(LinkMovementMethod.getInstance());
                        messageText.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                combankRatesImage.setImageResource(0);
                messageText.setText("");
            }
        });

        return v;
    }

    private void showWebViewDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        WebView webView = new WebView(getContext());
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Creating back button on alert dialog
        ImageButton backButton = new ImageButton(getContext());
        backButton.setImageResource(R.drawable.baseline_arrow_back_ios_24);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });
        RelativeLayout.LayoutParams backButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        backButtonParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        backButton.setLayoutParams(backButtonParams);

        // Creating forward button on alert dialog
        ImageButton forwardButton = new ImageButton(getContext());
        forwardButton.setImageResource(R.drawable.baseline_arrow_forward_ios_24);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });
        RelativeLayout.LayoutParams forwardButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        forwardButtonParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        forwardButton.setLayoutParams(forwardButtonParams);

        // Creating title layout
        RelativeLayout titleLayout = new RelativeLayout(getContext());
        titleLayout.addView(backButton);
        titleLayout.addView(forwardButton);

        builder.setCustomTitle(titleLayout);
        builder.setView(webView);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}