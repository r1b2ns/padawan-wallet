//
//  Network+Extensions.swift
//  PadawanWallet
//
//  Created by Rubens Machion on 02/08/25.
//
import BitcoinDevKit

extension Network {

    var kind: NetworkKind {
        switch self {
        case .bitcoin:
            return .main

        case .testnet, .testnet4, .signet, .regtest:
            return .test
        }
    }
}
