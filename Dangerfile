def isCapitalized(string)
  return string.slice(0,1).capitalize + string.slice(1..-1) == string
end

def lintCommit(commit)
  (subject, empty_line, *body) = commit.message.split("\n")
  message = "#{commit.sha} subject is malformed. You can use rebase to fix it\n"

  is_too_short = commit.message.length < 10
  is_too_long = subject.length > 72
  is_not_capitalized = !isCapitalized(subject)
  ends_with_dot = subject.split('').last == '.'

  is_failed = is_too_long || is_not_capitalized || ends_with_dot || is_too_short

  message += is_too_short ? ":warning: Subject is too short\n" : ""
  message += is_too_long ? ":warning: Subject is longer than 72 symbols\n" : ""
  message += is_not_capitalized ? ":warning: Subject is not capitalized\n" : ""
  message += ends_with_dot ? ":warning: Subject ends with dot\n" : ""

  fail(message) if is_failed
end

fail('Labels are not assigned') if gitlab.mr_labels.empty?
fail('Title should start from JIRA ticket id') unless gitlab.mr_title =~ /^[A-Z]+\-[0-9]+\s\w+$/ || gitlab.mr_labels.include?('common')
fail('Title is not capitalized') unless isCapitalized(gitlab.mr_title)
fail('Title is longer than 64 symbols (72 including PR number)') unless gitlab.mr_title.length <= 64

for commit in git.commits
	next if commit.message =~ /^Merge (remote-tracking )?branch/
  lintCommit(commit)
end

%w(gradle/wrapper/gradle-wrapper.properties
  settings.gradle build.gradle app/build.gradle
  Jenkinsfile-check Jenkinsfile-publish
  Dangerfile Gemfile Gemfile.lock
  signing/ debug.keystore
  .gitignore
).each { |file|  warn "#{gitlab.html_link(file)} was edited." if git.modified_files.include?(file) }
