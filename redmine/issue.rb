#!/usr/bin/ruby

require 'rubygems'
require 'active_resource'

class Issue < ActiveResource::Base
  self.site = 'https://3lm-support.brilliantservice.co.jp'
  self.user = '3lm_report'
  self.password = 'IYkLnxauzLfuAwPzfu3VmQil4iYyxXS8ZY9vn0l+mFI='
  
  def tr
    <<EOS
<tr>
  <td width=50>#{a_issue(self.id)}</td>
  <td width=100>#{self.component}</td>
  <td width=200>#{self.assigned_to.name}</td>
  <td width=600>#{self.subject}</td>
  <td width=600>#{self.related_issues}</td>
</tr>
EOS
  end

  def related_issues
    self.relations.collect do |r|
      a_issue((self.id==r.issue_id)? r.issue_to_id: r.issue_id)
    end.join(' , ')
  end

  def component
    ret = self.custom_fields.select{|cf| cf.name == 'Component'}
    (ret.size > 0)? ret[0].value : ''
  end

  def a_issue(id)
    "<a href=https://3lm-support.brilliantservice.co.jp/issues/#{id}>#{id}</a>"
  end

  def is_bug
    self.tracker.name == 'Bug'
  end
  def is_open
    ! self.is_closed()
  end
  def is_closed
    sname = self.status.name
    sname == 'Resolved' || sname == 'Closed' || sname == 'Rejected'
  end
end







