//
//  PaySetuSDK.swift
//  PaySetu Indian Digital Payments SDK for iOS
//  Compliant with NPCI UPI iOS Linking Specification
//

import UIKit

public struct PaySetuPaymentResult {
    public let paymentId: String
    public let utr: String
    public let amount: Double
    public let status: String
}

public enum PaySetuError: Error {
    case invalidParameters
    case upiAppNotInstalled
    case paymentDeclined(String)
    case networkError
}

public class PaySetuSDK {
    public static let shared = PaySetuSDK()
    private init() {}

    /**
     * Launches the NPCI UPI Intent Flow on iOS
     * Generates compliant URL scheme: upi://pay?pa=...&pn=...&am=...
     */
    public func startPayment(
        orderId: String,
        amount: Double,
        merchantVpa: String,
        merchantName: String,
        transactionNote: String = "Order Payment",
        completion: @escaping (Result<PaySetuPaymentResult, PaySetuError>) -> Void
    ) {
        guard amount > 0, !merchantVpa.isEmpty else {
            completion(.failure(.invalidParameters))
            return
        }

        let formattedAmount = String(format: "%.2f", amount)
        guard let encodedName = merchantName.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed),
              let encodedNote = transactionNote.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) else {
            completion(.failure(.invalidParameters))
            return
        }

        let upiUrlString = "upi://pay?pa=\(merchantVpa)&pn=\(encodedName)&am=\(formattedAmount)&cu=INR&tn=\(encodedNote)&tr=\(orderId)"
        guard let url = URL(string: upiUrlString) else {
            completion(.failure(.invalidParameters))
            return
        }

        // Open installed UPI app or PaySetu app
        if UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:]) { success in
                if success {
                    // Merchant server must poll /v1/payments/{paymentId} to independently verify!
                    // Client callback should never be trusted as sole source of truth.
                } else {
                    completion(.failure(.paymentDeclined("User cancelled payment")))
                }
            }
        } else {
            completion(.failure(.upiAppNotInstalled))
        }
    }
}
