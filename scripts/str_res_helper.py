import re
import os

NAME_PATTERN = re.compile('.+<string name="(.*)".+')
APP_SRC_DIR = '../app/src/main'
CORE_SRC_DIR = '../core/src/main'

def get_str_res_items(file_path):
	f = open(file_path)
	lines = f.readlines()
	f.close()
	if len(lines) <= 3:
		return []
	return lines[2:len(lines)-1]

def filter_res_in_file(file_path, needed_res_names):
	lines = get_str_res_items(file_path)
	resulted_lines = []
	for line in lines:
		res = NAME_PATTERN.findall(line)
		if len(res) > 0 and res[0] in needed_res_names:
			resulted_lines.append(line)
	print_src_res_to(file_path, resulted_lines)


def find_str_res_in_file(file_path, needed_res_names, remove_from_origin = False):
	matched_lines = []
	extra_lines = []
	lines = get_str_res_items(file_path)
	for line in lines:
		res = NAME_PATTERN.findall(line)
		if len(res) > 0 and res[0] in needed_res_names:
			matched_lines.append(line)
		else:
			extra_lines.append(line)
	if remove_from_origin:
		print_src_res_to(file_path, extra_lines)
	return matched_lines


def print_src_res_to(file_path, resources):
	with open(file_path, 'w') as f:
		f.write('<?xml version="1.0" encoding="UTF-8"?>\n')
		f.write('<resources>\n')
		for w_line in resources:
			f.write(w_line)
		f.write('</resources>')


def append_to_file(file_path, new_items, sort = False):
	cur_items = get_str_res_items(file_path)
	print ("Append items to %s" % file_path)
	print ("%s items exist into cur file" % len(cur_items))
	new_list = cur_items + new_items
	new_list = list(set(new_list))
	print ("%s items are wrote into cur file" % len(new_list))
	if sort:
		new_list.sort()
	print_src_res_to(file_path, new_list)

def fetch_res_names(res_list):
	result = []
	for line in res_list:
		res = NAME_PATTERN.findall(line)
		if len(res) > 0:
			result.append(res[0])
	return result

def fetch_missing_lines(source_path, destination_dath):
	source_res = get_str_res_items(source_path)
	for item in source_res:
		print(item)
	sources_name = fetch_res_names(source_res)
	print("S has %s/%s items" % (len(sources_name), len(source_res)))
	destination_res = get_str_res_items(destination_dath)
	destination_name = fetch_res_names(destination_res)
	print("D has %s/%s items" % (len(destination_name), len(destination_res)))
	return set(sources_name) - set(destination_name)

work_dir = CORE_SRC_DIR + '/res/'
folders = [f for f in os.listdir(work_dir) if f.startswith('values-')]

####sort all str in core
# sort_folders = [f for f in os.listdir(work_dir) if f.startswith('values')]
# print(sort_folders)
# for folder in sort_folders:
# 	path = work_dir + "%s/strings.xml" % folder
# 	lines = get_str_res_items(path)
# 	lines.sort()
# 	print_src_res_to(path, lines)

# # find differet between `values` str and values-xyz
# file_path_pattern = work_dir + "%s/strings.xml"
# for folder in folders:
# 	dest_path = file_path_pattern % folder
# 	res = fetch_missing_lines(file_path_pattern % "values", dest_path)
# 	print("%s file doesn't have items:" % dest_path)
# 	print(res)
# 	print("")

# print (existing_names)
# folders = [f for f in os.listdir(work_dir) if f.startswith('values-')]


# #remove redundant lines
# for folder in folders:
# 	print("Filter resources in %s" % folder)
# 	filter_res_in_file(work_dir + "/" + folder + "/strings.xml", existing_names)
# ####

## fetch items from another dirs
lines = get_str_res_items(work_dir + "values/strings.xml")
existing_names = fetch_res_names(lines)
file_patterns = [
	"/resSocial/%s/social_strings.xml",
	"/resSession/%s/session_strings.xml",
	"/res/%s/strings.xml",
	"/res/%s/dtl_strings.xml",
	"/resMessenger/%s/messenger_strings.xml"]

for folder in folders:
	items = []
	for file_pattern in file_patterns:
		file_name = APP_SRC_DIR + (file_pattern % folder)
		print("Find items into %s" % file_name)
		items_from_cur_file = find_str_res_in_file(file_name, existing_names, True)
		items.extend(items_from_cur_file)
		print("%s items  ware founded" % len(items_from_cur_file))

	append_to_file(work_dir + folder + "/strings.xml", items, True)
	# for item in res_items:
	# 	print("FOUND ", item)
	#   print("")
# Remove duplicated lines from values file
# file_name = APP_SRC_DIR + "/res/values/strings.xml"
# find_str_res_in_file(file_name, existing_names, True)
########
