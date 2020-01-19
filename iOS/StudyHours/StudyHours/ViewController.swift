//
//  ViewController.swift
//  StudyHours
//
//  Created by Shuang Li on 1/14/20.
//  Copyright © 2020 Shuang Li. All rights reserved.
//

import UIKit
import FirebaseAuth
import FirebaseDatabase

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    //MARK: Properties
    var ref = Database.database().reference()
    let user = Auth.auth().currentUser
    var sessions: [Int: Session] = [:]
    
    @IBOutlet weak var totalHoursText: UILabel!
    @IBOutlet weak var myHoursTableView: UITableView!
    @IBOutlet weak var actIndicator2: UIActivityIndicatorView!

    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        //normally it would be the length of an array controller.
        return sessions.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: .subtitle, reuseIdentifier: "MyTestCell")
        
        if let session = sessions[indexPath.row+1]{
            let dateIn = Date(timeIntervalSince1970: TimeInterval(Double((session.getIn())/1000)))
            let dateOut = Date(timeIntervalSince1970: TimeInterval(Double((session.getOut())/1000)))
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "MM/dd/yyyy HH:mm"
            let dateInFormatted = dateFormatter.string(from: dateIn)
            let dateOutFormatted = dateFormatter.string(from: dateOut)
            cell.textLabel?.text = dateInFormatted + " - " + dateOutFormatted
        }
//        cell.detailTextLabel?.text = "Subtitle \(indexPath.row)"
        return cell
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        actIndicator2.startAnimating()
        ref.child("hours").child(String(user!.uid)).observe(.value) { snapshot in
            for child in (snapshot.children.allObjects as! [DataSnapshot]){
                if (child.key == "totalHours"){
                    self.totalHoursText.text = "Total Hours: \(child.value!)"
                }else if (child.key != "numSessions" && child.key != "sisterName"){
                    var `in`: Int64 = 0
                    var out: Int64 = 0
                    for elem in (child.children.allObjects as! [DataSnapshot]){
                        if (elem.key == "in"){
                            `in` = elem.value as! Int64
                        }
                        if (elem.key == "out"){
                            out = elem.value as! Int64
                        }
                    }
                    if (out != 0){
                        let session = Session(`in`, out)
                        self.sessions[Int(child.key)!] = session
                    }
                }
            }
            self.myHoursTableView.reloadData()
            self.actIndicator2.stopAnimating()
        }
    }
    
}
