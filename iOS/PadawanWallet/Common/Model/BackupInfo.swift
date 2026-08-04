//
//  BackupInfo.swift
//  PadawanWallet
//
//  Created by Rubens Machion on 03/08/25.
//

import Foundation

struct BackupInfo: Codable, Equatable {
    var mnemonic: String
    var descriptor: String
    var changeDescriptor: String

    init(mnemonic: String, descriptor: String, changeDescriptor: String) {
        self.mnemonic = mnemonic
        self.descriptor = descriptor
        self.changeDescriptor = changeDescriptor
    }

    static func == (lhs: BackupInfo, rhs: BackupInfo) -> Bool {
        return lhs.mnemonic == rhs.mnemonic && lhs.descriptor == rhs.descriptor
            && lhs.changeDescriptor == rhs.changeDescriptor
    }
}

#if DEBUG
    import BitcoinDevKit

    extension BackupInfo {
        private static let mockMnemonic =
            "space echo position wrist orient erupt relief museum myself grain wisdom tumble"

        private static let mockNetwork: Network = .signet

        static var mock = makeMock()

        private static func makeMock() -> Self {
            do {
                let mnemonic = try Mnemonic.fromString(mnemonic: mockMnemonic)
                let secretKey = DescriptorSecretKey(
                    networkKind: mockNetwork.kind,
                    mnemonic: mnemonic,
                    password: nil
                )
                let descriptors = Descriptor.createDescriptors(
                    secretKey: secretKey,
                    network: mockNetwork
                )

                return Self(
                    mnemonic: mnemonic.description,
                    descriptor: descriptors.descriptor.toStringWithSecret(),
                    changeDescriptor: descriptors.changeDescriptor.toStringWithSecret()
                )
            } catch {
                fatalError("BackupInfo.mock: invalid mock mnemonic - \(error)")
            }
        }
    }
#endif
