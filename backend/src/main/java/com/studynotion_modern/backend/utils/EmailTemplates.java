package com.studynotion_modern.backend.utils;

public class EmailTemplates {
    public static String contactUsEmail(String email, String firstname, String lastname,
            String message, String phoneNo, String countrycode) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <title>Contact Form Confirmation</title>
                  <style>
                    body { background-color: #ffffff; font-family: Arial, sans-serif; font-size: 16px; color: #333333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; text-align: center; }
                    .message { font-size: 18px; font-weight: bold; margin-bottom: 20px; }
                    .body { font-size: 16px; margin-bottom: 20px; }
                    .support { font-size: 14px; color: #999999; margin-top: 20px; }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <img src="https://i.ibb.co/7Xyj3PC/logo.png" alt="StudyNotion Logo" style="max-width:200px;" />
                    <div class="message">Contact Form Confirmation</div>
                    <div class="body">
                      <p>Dear %s %s,</p>
                      <p>Thank you for contacting us. Here are the details you provided:</p>
                      <p>Email: %s</p>
                      <p>Phone Number: %s</p>
                      <p>Message: %s</p>
                    </div>
                    <div class="support">If you need help, reach out at <a href="mailto:info@studynotion.com">info@studynotion.com</a></div>
                  </div>
                </body>
                </html>
                """
                .formatted(firstname, lastname, email, phoneNo, message);
    }

    public static String buildOtpEmailTemplate(String otp) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
                .container { max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                .header { text-align: center; color: #333; margin-bottom: 30px; }
                .otp-box { background-color: #f8f9fa; border: 2px dashed #007bff; padding: 20px; text-align: center; margin: 20px 0; border-radius: 8px; }
                .otp-code { font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 5px; }
                .message { color: #666; line-height: 1.6; text-align: center; }
                .warning { color: #dc3545; font-size: 14px; margin-top: 20px; }
                .footer { text-align: center; margin-top: 30px; color: #999; font-size: 12px; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>StudyNotion Email Verification</h1>
                </div>
                <div class="message">
                    <p>Thank you for signing up with StudyNotion! To complete your registration, please use the following One-Time Password (OTP):</p>
                </div>
                <div class="otp-box">
                    <div class="otp-code">%s</div>
                </div>
                <div class="message">
                    <p>This OTP is valid for 10 minutes. Please do not share this code with anyone.</p>
                </div>
                <div class="warning">
                    <p>If you didn't request this verification, please ignore this email.</p>
                </div>
                <div class="footer">
                    <p>&copy; 2025 StudyNotion. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(otp);
    }
}
