//
//  Session.swift
//  StudyHours
//
//  Created by Shuang Li on 1/18/20.
//  Copyright © 2020 Shuang Li. All rights reserved.
//

import Foundation

class Session {
    
    var `in` : Int64
    var out : Int64

    init(_ `in`: Int64, _ out: Int64) {
        self.`in` = `in`
        self.out = out
    }
    
    func getIn() -> Int64{
        return self.`in`
    }
    
    func getOut() -> Int64{
        return self.out
    }
}
