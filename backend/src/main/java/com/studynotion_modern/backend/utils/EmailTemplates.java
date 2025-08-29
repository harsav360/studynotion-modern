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
}
