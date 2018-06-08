package com.tuzhao.publicwidget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuzhao.R;

/**
 * 提示对话框
 */


public class TipeDialog extends Dialog {

    private TextView mTextView;

    public TipeDialog(Context context) {
        super(context);
    }

    public TipeDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private CharSequence message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private TextView mTitleView;
        private TextView mNegativeView;
        private TextView mPositiveView;
        private boolean autoDissmiss = true;
        private boolean cancelable = true;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            if (mTitleView != null) {
                mTitleView.setText(title);
            }
            return this;
        }

        /**
         * Set the Dialog title from String
         */

        public Builder setTitle(String title) {
            this.title = title;
            if (mTitleView != null) {
                mTitleView.setText(title);
            }
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * @param autoDissmiss true(点击确认或取消按钮时自动取消对话框)
         */
        public Builder autoDissmiss(boolean autoDissmiss) {
            this.autoDissmiss = autoDissmiss;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;

            if (mPositiveView != null) {
                mPositiveView.setText(positiveButtonText);
            }
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;

            if (mPositiveView != null) {
                mPositiveView.setText(positiveButtonText);
            }
            return this;
        }

        public void setPositiveButtonText(String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
            if (mPositiveView != null) {
                mPositiveView.setText(positiveButtonText);
            }
        }

        public void setNegativeButtonText(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            if (mNegativeView != null) {
                mNegativeView.setText(negativeButtonText);
            }
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;

            if (mNegativeView != null) {
                mNegativeView.setText(negativeButtonText);
            }
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;

            if (mNegativeView != null) {
                mNegativeView.setText(negativeButtonText);
            }
            return this;
        }

        public TipeDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final TipeDialog dialog = new TipeDialog(context, R.style.Dialog);
            dialog.setCancelable(cancelable);
            View layout = inflater.inflate(R.layout.dialog_normal_layout_refator, null);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            mTitleView = layout.findViewById(R.id.title);
            mTitleView.setText(title);

            mPositiveView = layout.findViewById(R.id.positiveButton);
            mNegativeView = layout.findViewById(R.id.negativeButton);
            // set the confirm button
            if (positiveButtonText != null) {
                mPositiveView.setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    mPositiveView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                            if (autoDissmiss) {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            } else {
                mPositiveView.setText("确定");
            }

            if (negativeButtonText != null) {
                mNegativeView.setText(negativeButtonText);
            } else {
                mNegativeView.setText("取消");
            }
            (layout.findViewById(R.id.negativeButton))
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (negativeButtonClickListener != null) {
                                negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                            }
                            if (autoDissmiss) {
                                dialog.dismiss();
                            }
                        }
                    });

            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(
                        contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }

}
