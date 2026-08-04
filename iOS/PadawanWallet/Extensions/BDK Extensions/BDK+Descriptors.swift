//
//  BDK+Descriptors.swift
//  PadawanWallet
//
//  Created by Rubens Machion on 02/08/25.
//
import BitcoinDevKit

extension Descriptor {
    
    static func createDescriptors(
        secretKey: DescriptorSecretKey,
        network: Network
    ) -> (descriptor: Descriptor, changeDescriptor: Descriptor) {
        
        let descriptor = Descriptor.newBip84(
            secretKey: secretKey,
            keychainKind: .external,
            networkKind: network.kind
        )
        let changeDescriptor = Descriptor.newBip84(
            secretKey: secretKey,
            keychainKind: .internal,
            networkKind: network.kind
        )
        return (descriptor, changeDescriptor)
    }
    
    static func createPublicDescriptors(
        publicKey: DescriptorPublicKey,
        fingerprint: String,
        network: Network
    ) throws -> (descriptor: Descriptor, changeDescriptor: Descriptor) {
        let descriptor = try Descriptor.newBip84Public(
            publicKey: publicKey,
            fingerprint: fingerprint,
            keychainKind: .external,
            networkKind: network.kind
        )
        let changeDescriptor = try Descriptor.newBip84Public(
            publicKey: publicKey,
            fingerprint: fingerprint,
            keychainKind: .internal,
            networkKind: network.kind
        )
        return (descriptor, changeDescriptor)
    }
}
