//
//  ViewController.swift
//  StudyHours
//
//  Created by Shuang Li on 1/14/20.
//  Copyright © 2020 Shuang Li. All rights reserved.
//

import UIKit

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    //MARK: Properties
    @IBOutlet weak var totalHours: UILabel!
    @IBOutlet weak var myHoursTableView: UITableView!
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        //normally it would be the length of an array controller.
        //TODO: connect to firebase and read num of Sessions
        return 10
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: .subtitle, reuseIdentifier: "MyTestCell")
        cell.textLabel?.text = "Row \(indexPath.row)"
//        cell.detailTextLabel?.text = "Subtitle \(indexPath.row)"
        return cell
    }
    

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
}

